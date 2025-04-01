from fastapi import FastAPI, UploadFile, Depends
from sqlalchemy.orm import Session
from database import SessionLocal
from minio_client import upload_file

app = FastAPI()

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
