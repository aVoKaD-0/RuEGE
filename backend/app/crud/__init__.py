from .crud_user import user
from .crud_category import category
from .crud_content import content
from .crud_task import task
from .crud_task_option import task_option
from .crud_user_task_attempt import user_task_attempt
from .crud_progress import progress
from .crud_news import news

# Импортируйте сюда другие CRUD объекты по мере их создания
# from .crud_news import news
# ...

__all__ = [
    "user",
    "category",
    "content",
    "task",
    "task_option",
    "user_task_attempt",
    "progress",
    "news",
    # "news",
    # ...
]
