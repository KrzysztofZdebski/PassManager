import json
import random
import string

# Function to generate a random string of a given length
def generate_random_string(length):
    return ''.join(random.choices(string.ascii_letters + string.digits, k=length))

# Function to generate a single password entry
def generate_password_entry():
    return {
        "encryptedPassword": generate_random_string(44),
        "site": {
            "nameSite": generate_random_string(10) + ".com"
        }
    }

# Function to generate a single user entry with multiple password entries
def generate_user_entry(num_passwords):
    return [generate_password_entry() for _ in range(num_passwords)]

# Function to generate a JSON file with the desired structure
def generate_large_json_file(file_path, num_users, passwords_per_user):
    data = []  # Use a list to store each user as a separate JSON object
    for _ in range(num_users):
        username = generate_random_string(8)  # Generate a random username
        user_entry = {username: generate_user_entry(passwords_per_user)}  # Create a user object
        data.append(user_entry)  # Append the user object to the list
    
    # Write the data to a JSON file
    with open(file_path, "w") as file:
        json.dump(data, file, indent=4)
    print(f"Generated {num_users} users with {passwords_per_user} passwords each in {file_path}")

# Generate a JSON file with 10 users, each having 2 passwords
generate_large_json_file("large_passwords.json", 1000000, 2)