from sqlalchemy import String, Integer, TIMESTAMP, func, Text, ForeignKey
from sqlalchemy.orm import Mapped, mapped_column, relationship
from datetime import datetime
from typing import Optional, List, TYPE_CHECKING

from app.db.base import Base

if TYPE_CHECKING:
    from .content import Content
    from .task_option import TaskOption
    from .user_task_attempt import UserTaskAttempt


class Task(Base):
    __tablename__ = "tasks"

    task_id: Mapped[str] = mapped_column(String(255), primary_key=True)
    content_id: Mapped[Optional[str]] = mapped_column(String(255), ForeignKey("contents.content_id", ondelete="SET NULL"), nullable=True, index=True)
    title: Mapped[str] = mapped_column(String(255), nullable=False)
    description: Mapped[Optional[str]] = mapped_column(Text, nullable=True)
    difficulty: Mapped[int] = mapped_column(Integer, default=1, nullable=False)
    max_points: Mapped[int] = mapped_column(Integer, default=1, nullable=False)
    task_type: Mapped[str] = mapped_column(String(50), nullable=False, index=True)
    time_limit: Mapped[int] = mapped_column(Integer, default=0, nullable=False) # в секундах
    created_at: Mapped[datetime] = mapped_column(
        TIMESTAMP(timezone=True),
        server_default=func.now(),
        nullable=False
    )
    updated_at: Mapped[datetime] = mapped_column(
        TIMESTAMP(timezone=True),
        server_default=func.now(),
        onupdate=func.now(),
        nullable=False
    )

    # Связи
    content: Mapped[Optional["Content"]] = relationship()
    options: Mapped[List["TaskOption"]] = relationship("TaskOption", back_populates="task", cascade="all, delete-orphan")
    attempts: Mapped[List["UserTaskAttempt"]] = relationship("UserTaskAttempt", back_populates="task", cascade="all, delete-orphan")

    def __repr__(self) -> str:
        return f"<Task(task_id='{self.task_id}', title='{self.title}', type='{self.task_type}')>" 