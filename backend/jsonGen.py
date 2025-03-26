import json
import random
import string

count = 0

# Function to generate a random string of a given length
def generate_random_string(length):
    return ''.join(random.choices(string.ascii_letters + string.digits, k=length))

# Function to generate a single password entry
def generate_password_entry():
    global count
    print(count)
    count += 1
    return {
        "key": generate_random_string(64),
        "site": {
            "nameSite": generate_random_string(10) + ".com"
        },
        "user": generate_random_string(8),
        "encryptedPassword": generate_random_string(44)
    }

# Generate a large list of password entries
def generate_large_json_file(file_path, num_entries):
    data = [generate_password_entry() for _ in range(num_entries)]
    with open(file_path, "w") as file:
        json.dump(data, file, indent=4)
    print(f"Generated {num_entries} entries in {file_path}")

# Generate a JSON file with 10,000 entries
generate_large_json_file("large_passwords.json", 1000000)