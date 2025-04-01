import os

DATABASE_URL = "postgresql+asyncpg://user:password@db/mydatabase"
JWT_SECRET = os.getenv("JWT_SECRET", "mysecret")
