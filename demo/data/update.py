import os
from datetime import datetime
import mysql.connector

#%%

def get_info(data_folder):
    result = []
    current_date = datetime.now().strftime('%Y-%m-%d')
    for subdir in os.listdir(data_folder):
        subdir_path = os.path.join(data_folder, subdir)
        if os.path.isdir(subdir_path):
            subdir_info = [subdir]
            md_text = ''
            txt_text = ''
            jpg_name = ''
            for file in os.listdir(subdir_path):
                file_path = os.path.join(subdir_path, file)
                if file.endswith('.md'):
                    with open(file_path, 'r') as f:
                        lines = f.readlines()
                        for line in lines:
                            if not line.startswith('#') and not line.startswith('*'):
                                md_text += line.strip()
                            if len(md_text) >= 50:
                                break
                elif file.endswith('.txt'):
                    with open(file_path, 'r') as f:
                        txt_text += f.read()
                elif file.endswith('.jpg'):
                    jpg_name = file
            subdir_info.append(md_text[:50])
            subdir_info.append(current_date)
            subdir_info.append(txt_text[:3])
            subdir_info.append(jpg_name)
            result.append(subdir_info)
    return result

#%% MySQL

def insert_data(data, host, user, password, database):
    # Connect to the database
    cnx = mysql.connector.connect(
        host=host,
        user=user,
        password=password,
        database=database
    )
    cursor = cnx.cursor()
    
    # Define the insert statement
    insert_stmt = (
        "INSERT IGNORE INTO blogposts (title, summary, date, rating, picture) "
        "VALUES (%s, %s, %s, %s, %s)"
    )
    
    # Insert the data
    for row in data:
        title = row[0]
        summary = row[1]
        date = row[2]
        rating = row[3]
        picture = row[4]
        data = (title, summary, date, rating, picture)
        cursor.execute(insert_stmt, data)
    
    # Commit the changes and close the connection
    cnx.commit()
    cursor.close()
    cnx.close()

#%% Main

data_folder = './'
data = get_info(data_folder)
print(data)

host='localhost'
user='dylan'
password='phthalo'
database = 'content'
insert_data(data, host, user, password, database)
