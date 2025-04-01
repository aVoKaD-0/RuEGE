from fastapi import APIRouter, Depends
from app.models import User
from app.schemas import UserCreate, Token
from app.services import create_user, authenticate_user, get_current_user

router = APIRouter()

@router.post("/register", response_model=Token)
async def register(user_data: UserCreate):
    return await create_user(user_data)

@router.post("/login", response_model=Token)
async def login(user_data: UserCreate):
    return await authenticate_user(user_data)

@router.get("/me", response_model=User)
async def me(user: User = Depends(get_current_user)):
    return user
