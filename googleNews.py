import requests
from bs4 import BeautifulSoup
import pymysql
import yaml
import warnings
from datetime import datetime

categories = {
    "politics": "https://news.google.com/rss/search?q=%EC%A0%95%EC%B9%98&hl=ko&gl=KR&ceid=KR%3Ako",
    "economy": "https://news.google.com/rss/search?q=%EA%B2%BD%EC%A0%9C&hl=ko&gl=KR&ceid=KR%3Ako",
    "culture": "https://news.google.com/rss/topics/CAAqIQgKIhtDQkFTRGdvSUwyMHZNR3QwTlRFU0FtdHZLQUFQAQ?hl=ko&gl=KR&ceid=KR%3Ako",
    "science": "https://news.google.com/rss/topics/CAAqKAgKIiJDQkFTRXdvSkwyMHZNR1ptZHpWbUVnSnJieG9DUzFJb0FBUAE?hl=ko&gl=KR&ceid=KR%3Ako",
    "entertainment": "https://news.google.com/rss/topics/CAAqJggKIiBDQkFTRWdvSUwyMHZNREpxYW5RU0FtdHZHZ0pMVWlnQVAB?hl=ko&gl=KR&ceid=KR%3Ako",
    "sports": "https://news.google.com/rss/topics/CAAqJggKIiBDQkFTRWdvSUwyMHZNRFp1ZEdvU0FtdHZHZ0pMVWlnQVAB?hl=ko&gl=KR&ceid=KR%3Ako"
}

with open("C:/NewsCrawling/db_config.yaml", "r", encoding="utf-8") as file:
    db_config = yaml.safe_load(file)

connection = pymysql.connect(
    host=db_config["host"],
    user=db_config["user"],
    password=db_config["password"],
    database=db_config["database"]
)

def convert_to_datetime(pubDate):
    """RSS에서 제공되는 날짜를 MySQL DATETIME 형식으로 변환"""
    try:
        return datetime.strptime(pubDate, "%a, %d %b %Y %H:%M:%S GMT").strftime("%Y-%m-%d %H:%M:%S")
    except ValueError:
        return None

try:
    cursor = connection.cursor()

    cursor.execute("SELECT MAX(id) FROM news")
    last_id = cursor.fetchone()[0]

    if last_id is None:
        new_id = 1
    else:
        new_id = last_id + 1

    for category, url in categories.items():
        print(f"크롤링 시작: {category} 카테고리")

        response = requests.get(url)
        soup = BeautifulSoup(response.content, "lxml-xml")

        items = soup.find_all("item")

        for item in items:
            title = item.find("title").text.strip()
            description = item.find("description").text.strip()
            link = item.find("link").text.strip()

            pubDate_tag = item.find("pubDate")
            pubDate = pubDate_tag.text.strip() if pubDate_tag else "날짜 없음"

            formatted_date = convert_to_datetime(pubDate) if pubDate != "날짜 없음" else None

            if '-' in title:
                title_part, publisher_part = title.split('-', 1)
                title_part = title_part.strip()
                publisher_part = publisher_part.strip()
            else:
                title_part = title
                publisher_part = ""
            
            
            cursor.execute("SELECT COUNT(*) FROM news WHERE title = %s", (title_part,))
            title_exists = cursor.fetchone()[0]


            if title_exists == 0:
                sql = """
                INSERT INTO news (id, title, publisher, date, link, category) 
                VALUES (%s, %s, %s, %s, %s, %s)
                """

                cursor.execute(sql, (new_id, title_part, publisher_part, formatted_date, link, category))

                new_id += 1
            else:
                print(f"제목 '{title_part}'은 이미 존재합니다.")

    connection.commit()

    print("모든 뉴스가 성공적으로 데이터베이스에 추가되었습니다.")

except pymysql.MySQLError as e:
    print(f"데이터베이스 오류: {e}")

finally:
    if 'cursor' in locals():
        cursor.close()
    if 'connection' in locals():
        connection.close()