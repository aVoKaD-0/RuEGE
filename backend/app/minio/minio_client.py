from minio import Minio
import os

minio_client = Minio(
    os.getenv("MINIO_ENDPOINT", "minio:9000"),
    access_key=os.getenv("MINIO_ACCESS_KEY", "minioadmin"),
    secret_key=os.getenv("MINIO_SECRET_KEY", "minioadmin"),
    secure=os.getenv("MINIO_SECURE", "false").lower() == "true"
)

def upload_file(file_name: str, file_data: bytes):
    bucket_name = os.getenv("MINIO_BUCKET", "books")
    minio_client.put_object(bucket_name, file_name, file_data, len(file_data))
    return f"/minio/{bucket_name}/{file_name}" 