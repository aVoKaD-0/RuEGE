import os
from pydantic_settings import BaseSettings
from pydantic import PostgresDsn, computed_field
from typing import Any


class Settings(BaseSettings):
    # Путь к файлу .env относительно корня проекта
    # Мы ожидаем, что .env будет лежать в директории database/
    # Поэтому поднимаемся на уровень выше из backend/app/core
    # и идем в database/
    env_file_path: str = os.path.join(os.path.dirname(__file__), "..", "..", "..", "database", ".env")

    # Префикс для API эндпоинтов
    API_V1_STR: str = "/api/v1"

    # Настройки JWT
    SECRET_KEY: str = "your_super_secret_key"  # ОБЯЗАТЕЛЬНО СМЕНИТЬ!
    ALGORITHM: str = "HS256"
    ACCESS_TOKEN_EXPIRE_MINUTES: int = 30
    REFRESH_TOKEN_EXPIRE_DAYS: int = 7

    # Настройки базы данных (берутся из .env)
    POSTGRES_USER: str
    POSTGRES_PASSWORD: str
    POSTGRES_DB: str
    POSTGRES_HOST: str = "localhost" # По умолчанию localhost для локальной разработки
    POSTGRES_PORT: int = 5432

    # Вычисляемое поле для SQLAlchemy Database URL
    @computed_field
    @property
    def SQLALCHEMY_DATABASE_URI(self) -> PostgresDsn:
        """Собирает строку подключения к БД."""
        return PostgresDsn.build(
            scheme="postgresql+asyncpg",
            username=self.POSTGRES_USER,
            password=self.POSTGRES_PASSWORD,
            host=self.POSTGRES_HOST,
            port=self.POSTGRES_PORT,
            path=self.POSTGRES_DB,
        )

    # Настройки для Alembic (опционально)
    # ALEMBIC_CONFIG: str = "alembic.ini"

    class Config:
        env_file = env_file_path
        env_file_encoding = 'utf-8'
        case_sensitive = True # Важно для имен переменных окружения


settings = Settings() 