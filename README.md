## Start
### 1. Clone the git repository
```git
git clone https://github.com/Cueerno/UnmatchedTracker.git
```

### 2. Set up environment <br>
Find the file .env.example and rename it to .env and fill it with your data (database password, etc.) <br>
*.env.example -> .env*
```dotenv
# PostgreSQL
POSTGRES_DB={your_database_name}
POSTGRES_USER={your_database_username}
POSTGRES_PASSWORD={your_database_password}
```

### 3. Set up Spring <br>
Find the file application.properties.example and rename it to application.properties. And fill your data <br>
*application.properties.example -> application.properties*

### 4. Set up Docker Compose
```docker
docker-compose up --build
```
or (in latest versions)
```docker
docker compose up --build
```

### 5. Execute sql scripts

### 6. Run Program