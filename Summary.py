from openai import OpenAI
import mysql.connector
import yaml
import time
import json
import yaml

with open("db_config.yaml", "r") as f:
    config = yaml.safe_load(f)

api_key = config['openai']['api_key']
def connect_db():
    with open("C:/NewsCrawling/db_config.yaml", "r", encoding="utf-8") as file:
        db_config = yaml.safe_load(file)

    return mysql.connector.connect(
        host=db_config["host"],
        user=db_config["user"],
        password=db_config["password"],
        database=db_config["database"]
    )

def fetch_unsummarized_content():
    conn = connect_db()
    cursor = conn.cursor(dictionary=True)

    query = """
        SELECT c.news_id, c.content
        FROM content c
        LEFT JOIN news n ON c.news_id = n.id
        WHERE n.summary IS NULL
    """
    cursor.execute(query)
    results = cursor.fetchall()

    cursor.close()
    conn.close()
    return results

def summarize_text(text):
    prompt = f"다음 뉴스 기사를 3줄로 요약해줘{text[:4000]}"
    try:
        response = client.chat.completions.create(
            model="gpt-4-1106-preview",
            messages=[ 
                {"role": "system", "content": "너는 한국어 뉴스 요약 도우미야."},
                {"role": "user", "content": prompt}
            ],
            max_tokens=500,
            temperature=0.7,
        )
        raw_summary = response.choices[0].message.content.strip()
        print("원본 응답:", raw_summary)

        summary_lines = [line.strip() for line in raw_summary.split('\n') if line.strip()]
        summary_lines = summary_lines[:3]
        
        return "\n".join(summary_lines)

    except Exception as e:
        print(f"요약 실패: {e}")
        return None

def update_summary(news_id, summary):
    print(f"{'-'*40}\n{summary}\n{'-'*40}")

    conn = connect_db()
    cursor = conn.cursor()
    try:
        query = "UPDATE news SET summary = %s WHERE id = %s"
        cursor.execute(query, (summary, news_id))
        conn.commit()
        print(f"[저장 완료] news_id: {news_id}")
    except Exception as e:
        print(f"DB 저장 오류: {e}")
    finally:
        cursor.close()
        conn.close()

def summarize_all():
    data = fetch_unsummarized_content()

    for row in data:
        news_id = row['news_id']
        content = row['content']

        print(f"요약 중 : news_id: {news_id}")
        summary = summarize_text(content)

        if summary:
            update_summary(news_id, summary)
        else:
            print(f"요약 실패 - news_id: {news_id}")
        
        time.sleep(1.5)

if __name__ == "__main__":
    summarize_all()
