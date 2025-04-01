from sqlalchemy.ext.asyncio import create_async_engine, AsyncSession
from sqlalchemy.orm import sessionmaker

from app.core.config import settings

# Создаем асинхронный движок SQLAlchemy
engine = create_async_engine(
    str(settings.SQLALCHEMY_DATABASE_URI), # SQLAlchemy требует строку, а не объект Pydantic
    pool_pre_ping=True, # Проверять соединение перед использованием
    echo=False # Установи в True для логирования SQL запросов (полезно для отладки)
)

# Создаем фабрику асинхронных сессий
AsyncSessionFactory = sessionmaker(
    bind=engine,
    class_=AsyncSession,
    expire_on_commit=False, # Не сбрасывать объекты после коммита
    autocommit=False,
    autoflush=False,
)


async def get_db() -> AsyncSession:
    """Зависимость FastAPI для получения асинхронной сессии БД."""
    async with AsyncSessionFactory() as session:
        try:
            yield session
            # Коммит не нужен здесь, он должен быть в CRUD операциях
            # await session.commit()
        except Exception:
            await session.rollback()
            raise
        finally:
            await session.close() 