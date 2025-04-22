import yaml
import mysql.connector
from newspaper import Article
from selenium import webdriver
from selenium.webdriver.chrome.options import Options
from selenium.webdriver.common.by import By
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

def fetch_news_links():
    conn = connect_db()
    cursor = conn.cursor(dictionary=True)
    cursor.execute("""
        SELECT id, link 
        FROM news 
        WHERE summary IS NULL 
        AND id NOT IN (SELECT news_id FROM content)
    """)
    results = cursor.fetchall()
    cursor.close()
    conn.close()
    return results

def get_real_url_from_google_news(link, driver):
    try:
        driver.get(link)
        time.sleep(2)
        return driver.current_url
    except Exception as e:
        print(f"리디렉션 실패: {e}")
        return None

def extract_article_text(url):
    try:
        article = Article(url, language='ko')
        article.download()
        article.parse()
        return article.text
    except Exception as e:
        print(f"본문 추출 실패: {e}")
        return None

def save_article_content(news_id, content):
    conn = connect_db()
    cursor = conn.cursor()
    query = "INSERT INTO content (news_id, content) VALUES (%s, %s)"
    cursor.execute(query, (news_id, content))
    conn.commit()
    cursor.close()
    conn.close()
    print(f"저장 완료 - news_id={news_id}")
    print(f"{'-'*60}\n{content}\n{'-'*60}")

def delete_failed_news(news_id):
    conn = connect_db()
    cursor = conn.cursor()
    query = "DELETE FROM news WHERE id = %s"
    cursor.execute(query, (news_id,))
    conn.commit()
    cursor.close()
    conn.close()
    print(f"삭제 완료 - 크롤링 실패한 news_id={news_id}")

def main():
    options = Options()
    options.add_argument('--headless')
    options.add_argument('--disable-gpu')
    options.add_argument('--no-sandbox')

    driver = webdriver.Chrome(options=options)

    news_items = fetch_news_links()

    for item in news_items:
        news_id = item['id']
        link = item['link']

        real_url = get_real_url_from_google_news(link, driver)

        if not real_url:
            print(f"[{news_id}] 리디렉션 실패")
            delete_failed_news(news_id)
            continue

        print(f"실제 뉴스 URL: {real_url}")

        text = extract_article_text(real_url)

        if text:
            print(f"[{news_id}] 본문 크롤링\n")
            print(f"{'='*80}\n{text}\n{'='*80}")
            save_article_content(news_id, text)
        else:
            print(f"[{news_id}] 본문 크롤링 실패")
            delete_failed_news(news_id)

        time.sleep(1)
    driver.quit()

if __name__ == "__main__":
    main()
