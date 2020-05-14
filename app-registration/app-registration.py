from secrets import randbelow
import mysql.connector

def rand_string_gen(length):
    chars = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789"
    pw = ""
    for _ in range(0, length):
        pw += chars[randbelow(len(chars))]
    return pw

def add_app_registration(client_id, redirect_uri, client_secret, connection):
    cursor = connection.cursor()
    data = (client_id, redirect_uri, client_secret)
    query = (
        "INSERT INTO `auth.apps`"
        "(client_id, redirect_uri, client_secret)"
        "VALUES"
        "(%s, %s, %s)"
    )
    cursor.execute(query, data)
    connection.commit()
    cursor.close()

redirect_uri = input("Paste your redirect URL: ")
client_id = rand_string_gen(12)
client_secret_len = 32 + randbelow(19)
client_secret = rand_string_gen(client_secret_len)
acc_tok_secret = rand_string_gen(128)

connection = mysql.connector.connect(user="localDev", password="l0c4l_d3v!", database="courier")
add_app_registration(client_id, redirect_uri, client_secret, connection)
connection.close()

print("Client ID is:")
print(client_id)
print("Client secret is " + str(client_secret_len) + " characters long and is:")
print(client_secret)
print("Access token secret is:")
print(acc_tok_secret)
