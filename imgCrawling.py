import mysql.connector
import yaml
from selenium import webdriver
from selenium.webdriver.chrome.service import Service
from selenium.webdriver.common.by import By
from selenium.common.exceptions import NoSuchElementException, WebDriverException
from webdriver_manager.chrome import ChromeDriverManager
import time

def connect_db():
    with open("C:/NewsCrawling/db_config.yaml", "r", encoding="utf-8") as file:
        db_config = yaml.safe_load(file)
        
    return mysql.connector.connect(
        host=db_config["host"],
        user=db_config["user"],
        password=db_config["password"],
        database=db_config["database"]
    )

def get_image_url(driver, link, news_id=None):
    try:
        driver.get(link)
        time.sleep(2)
        try:
            image = driver.find_element(By.XPATH, "//meta[@property='og:image']")
            img_url = image.get_attribute("content")
            if img_url and 'lh3.googleusercontent.com' not in img_url:
                print(f"성공 ID {news_id}, 이미지 URL: {img_url}")
                return img_url
            else:
                print(f"제외 ID {news_id}: Google 기본 이미지")
        except NoSuchElementException:
            print(f"실패 ID {news_id}: 대표 이미지 없음")
    except WebDriverException as e:
        print(f"오류 ID {news_id}: WebDriver 오류: {e}")
    return None

def update_img_url(db_conn, news_id, img_url):
    cursor = db_conn.cursor()
    try:
        sql = "UPDATE news SET img_url = %s WHERE id = %s"
        cursor.execute(sql, (img_url, news_id))
        db_conn.commit()
    except Exception as e:
        print(f"DB 조회 오류: {e}")
    finally:
        cursor.close()

def crawl_and_update_images():
    db_conn = connect_db()
    cursor = db_conn.cursor(dictionary=True)
    try:
        cursor.execute("SELECT id, link FROM news WHERE img_url IS NULL")
        rows = cursor.fetchall()

        options = webdriver.ChromeOptions()
        options.add_argument('--headless')
        driver = webdriver.Chrome(service=Service(ChromeDriverManager().install()), options=options)

        for row in rows:
            img_url = get_image_url(driver, row['link'], row['id'])
            if img_url:
                update_img_url(db_conn, row['id'], img_url)

        driver.quit()
    except Exception as e:
        print(f"DB 조회 오류: {e}")
    finally:
        cursor.close()
        db_conn.close()

if __name__ == '__main__':
    crawl_and_update_images()
