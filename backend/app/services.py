from passlib.context import CryptContext
from datetime import datetime, timedelta
import jwt
from app.models import User
from sqlalchemy.orm import Session

pwd_context = CryptContext(schemes=["bcrypt"], deprecated="auto")

def hash_password(password: str):
    return pwd_context.hash(password)

def verify_password(password: str, hashed: str):
    return pwd_context.verify(password, hashed)

def create_access_token(data: dict):
    return jwt.encode(data, "mysecret", algorithm="HS256")
