from fastapi import APIRouter

# Импортируем все роутеры эндпоинтов
from app.api.endpoints import (
    auth, 
    categories, 
    content, 
    tasks, 
    attempts,
    progress,
    news
)

# Основной роутер API v1
api_router = APIRouter(prefix="/api/v1") # Добавляем префикс /api/v1 ко всем эндпоинтам

# Подключаем роутеры для разных модулей
api_router.include_router(auth.router, prefix="/auth", tags=["Auth"])
api_router.include_router(categories.router, prefix="/categories", tags=["Categories"])
api_router.include_router(content.router, prefix="/content", tags=["Content"])
api_router.include_router(tasks.router, prefix="/tasks", tags=["Tasks"])
api_router.include_router(attempts.router, prefix="/attempts", tags=["Task Attempts"])
api_router.include_router(progress.router, prefix="/progress", tags=["Progress"])
api_router.include_router(news.router, prefix="/news", tags=["News"])

# Сюда будем добавлять другие роутеры (для задач, новостей и т.д.)
# api_router.include_router(tasks.router, prefix="/tasks", tags=["Tasks"])
# ...