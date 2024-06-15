import mysql.connector

def test_db_connection(db_config):
    try:
        # 데이터베이스 연결
        conn = mysql.connector.connect(**db_config)
        cursor = conn.cursor()
        cursor.execute('SELECT DATABASE()')
        result = cursor.fetchone()
        
        print(f"Connected to database: {result}")
        
        # 연결 종료
        conn.close()
    except mysql.connector.Error as err:
        print(f"Error: {err}")

# DB 설정
db_config = {
    'user': 'coddl',
    'password': '1234',
    'host': '43.201.173.245',
    'database': 'news'
}

test_db_connection(db_config)
