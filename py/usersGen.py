import requests
import random
import string
import urllib3
import json  # Import the JSON module
import time

urllib3.disable_warnings(urllib3.exceptions.InsecureRequestWarning)

def generate_random_string(length):
    return ''.join(random.choices(string.ascii_letters + string.digits, k=length))

# Function to generate a single password entry
def generate_password_entry(username):
    return {
        "siteName": generate_random_string(7) + ".com",
        "passwordName": generate_random_string(15),
        "user": username
    }

# Generate data and write to keys.json
with open("keys.json", "w") as f:
    all_data = {}  # Create a dictionary to store all users and their data
    for i in range(10000):
        print(i)
        username = f"user{i}"
        data = {}
        for j in range(10):
            password_entry = generate_password_entry(username)
            response = requests.post(
                f"https://localhost:5001/api/passwords/save?siteName={password_entry['siteName']}&passwordName={password_entry['passwordName']}&user={password_entry['user']}",
                verify=False
            )
            data[j] = {password_entry['siteName']: response.text}
        all_data[username] = data  # Add the user's data to the main dictionary
        time.sleep(1)

    # Write the entire dictionary as JSON to the file
    json.dump(all_data, f, indent=4)