import mysql.connector
import yaml
from selenium import webdriver
from selenium.webdriver.chrome.service import Service
from selenium.webdriver.common.by import By
from selenium.common.exceptions import NoSuchElementException, WebDriverException
from webdriver_manager.chrome import ChromeDriverManager
import time

# DB 연결 함수
def connect_db():
    with open("C:/NewsCrawling/db_config.yaml", "r", encoding="utf-8") as file:
        db_config = yaml.safe_load(file)
    return mysql.connector.connect(
        host=db_config["host"],
        user=db_config["user"],
        password=db_config["password"],
        database=db_config["database"]
    )

# 대표 이미지 크롤링 함수
def get_image_url(link):
    try:
        options = webdriver.ChromeOptions()
        options.add_argument('--headless')
        driver = webdriver.Chrome(service=Service(ChromeDriverManager().install()), options=options)
        driver.get(link)
        time.sleep(2)  # 페이지 로딩 대기

        # 이미지 추출 로직 (예: Open Graph 메타태그)
        try:
            image = driver.find_element(By.XPATH, "//meta[@property='og:image']")
            img_url = image.get_attribute("content")
            if 'lh3.googleusercontent.com' not in img_url:  # 기본 구글 이미지 필터링
                driver.quit()
                return img_url
        except NoSuchElementException:
            print("대표 이미지 없음")
        driver.quit()
    except WebDriverException as e:
        print(f"WebDriver 오류: {e}")
        return None

# DB 업데이트 함수
def update_img_url(db_conn, news_id, img_url):
    cursor = db_conn.cursor()
    try:
        sql = "UPDATE news SET img_url = %s WHERE id = %s"
        # cursor.execute(sql, (img_url, news_id))  # 실제 DB 업데이트 주석 처리
        # db_conn.commit()  # 실제 DB 커밋 주석 처리
        print(f"[테스트 모드] ID {news_id}: {img_url}")
    except Exception as e:
        print(f"DB 업데이트 오류: {e}")
    finally:
        cursor.close()

# 대표 이미지 없는 뉴스 데이터 크롤링 및 업데이트
def crawl_and_update_images():
    db_conn = connect_db()
    cursor = db_conn.cursor(dictionary=True)
    try:
        cursor.execute("SELECT id, link FROM news WHERE img_url IS NULL")
        rows = cursor.fetchall()
        for row in rows:
            img_url = get_image_url(row['link'])
            if img_url:
                update_img_url(db_conn, row['id'], img_url)
    except Exception as e:
        print(f"DB 조회 오류: {e}")
    finally:
        cursor.close()
        db_conn.close()

if __name__ == '__main__':
    crawl_and_update_images()
