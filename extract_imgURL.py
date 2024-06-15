import schedule
import time
from newspaper import Article
from newspaper.article import ArticleException
import mysql.connector

# 데이터베이스 연결 및 URL 가져오기 (모든 URL 가져오기)
def get_all_urls_from_db(db_config):
    try:
        print("데이터베이스에 연결 중")
        conn = mysql.connector.connect(**db_config)
        cursor = conn.cursor()

        print("데이터베이스에서 모든 URL을 가져오는 중")
        cursor.execute('SELECT news_id, link, img_url FROM news')
        urls = cursor.fetchall()

        print("데이터베이스 연결 종료 중")
        conn.close()

        print(f"데이터베이스에서 {len(urls)}개의 URL을 가져왔습니다.")
        return urls
    except mysql.connector.Error as err:
        print(f"데이터베이스 오류: {err}")
        return []

# 이미지 크롤링
def extract_image_url(url, retries=2):
    for attempt in range(retries):
        try:
            print(f"URL에서 이미지를 추출 중: {url} (시도 {attempt + 1}회)")
            article = Article(url)
            article.download()
            article.parse()
            
            top_image = article.top_image if article.top_image else None
            if top_image:
                print(f"URL에서 찾은 이미지: {top_image}")
            else:
                print(f"URL에서 이미지를 찾지 못했습니다: {url}")

            return top_image
        except ArticleException as e:
            log_error(url, f"처리 중 오류: {e}")
        except Exception as e:
            log_error(url, f"예기치 않은 오류: {e}")
        if attempt < retries - 1:
            print("다시 시도 중")
    return None

# 크롤링한 이미지 주소 저장
def save_image_url_to_db(db_config, news_id, new_image_url):
    try:
        print(f"news_id: {news_id}의 이미지 URL을 데이터베이스에 저장 중")
        conn = mysql.connector.connect(**db_config)
        cursor = conn.cursor()

        cursor.execute("UPDATE news SET img_url = %s WHERE news_id = %s", (new_image_url, news_id))
        conn.commit()
        
        print(f"news_id: {news_id}의 이미지 URL이 데이터베이스에 성공적으로 저장되었습니다.")
        conn.close()
    except mysql.connector.Error as err:
        print(f"데이터베이스 오류: {err}")
        log_error(news_id, f"MySQL Error: {err}")

def log_error(identifier, message):
    with open("error_log.txt", "a") as f:
        f.write(f"Identifier: {identifier}\nError: {message}\n\n")

# DB 설정
db_config = {
    'user': 'coddl',
    'password': '1234',
    'host': '43.201.173.245',
    'database': 'news'
}

def update_images():
    # DB에서 모든 URL 가져오기
    urls = get_all_urls_from_db(db_config)

    # 이미지 URL 추출 및 데이터베이스에 저장
    for idx, (news_id, url, current_image_url) in enumerate(urls):
        print(f"URL {idx + 1} 처리 중: {url}")
        new_image_url = extract_image_url(url)
        if new_image_url is None:
            new_image_url = None  # 이미지 URL이 없으면 None으로 설정 (NULL로 저장됨)
        if new_image_url != current_image_url:
            save_image_url_to_db(db_config, news_id, new_image_url)
            print("\n")
        else:
            print(f"이미지 URL이 변경되지 않았거나 이미 존재합니다: {url}")

# 스케줄 설정
schedule.every(30).minutes.do(update_images)

print("스케줄러가 설정되었습니다. 서버 시작 시 한 번 실행하고 이후 30분마다 업데이트가 실행됩니다.")

# 서버 시작 시 한 번 실행
update_images()

# 스케줄 실행
while True:
    schedule.run_pending()
    time.sleep(1)
