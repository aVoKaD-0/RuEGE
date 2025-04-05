from fastapi import FastAPI, UploadFile, Depends
from sqlalchemy.orm import Session
from app.db.session import SessionLocal
from app.minio.minio_client import upload_file
from fastapi.middleware.cors import CORSMiddleware

from app.api.api import api_router
from app.core.config import settings

app = FastAPI(
    title="RuEGE API",
    openapi_url=f"{settings.API_V1_STR}/openapi.json"
)

# Настройка CORS (Cross-Origin Resource Sharing)
# Разрешаем запросы с любых источников (для разработки)
# В production следует указать конкретные разрешенные домены
origins = [
    "*", # Для разработки. Замените на адреса вашего фронтенда в production
    # "http://localhost:8080", # Пример для Android Emulator localhost
    # "http://10.0.2.2:8080", # Пример для Android Emulator host machine
]

app.add_middleware(
    CORSMiddleware,
    allow_origins=origins,
    allow_credentials=True,
    allow_methods=["*"], # Разрешить все методы
    allow_headers=["*"], # Разрешить все заголовки
)

app.include_router(api_router)

@app.get("/")
async def root():
    return {"message": "Welcome to RuEGE API!"}

def get_db():
    db = SessionLocal()
    try:
        yield db
    finally:
        db.close()

@app.post("/upload/")
async def upload_book(file: UploadFile, db: Session = Depends(get_db)):
    content = await file.read()
    file_url = upload_file(file.filename, content)
    return {"url": file_url}
