from minio import Minio

minio_client = Minio(
    "minio:9000",
    access_key="minioadmin",
    secret_key="minioadmin",
    secure=False
)

def upload_file(file_name: str, file_data: bytes):
    minio_client.put_object("books", file_name, file_data, len(file_data))
    return f"http://localhost:9000/books/{file_name}"
