from sqlalchemy import String, BigInteger, Integer, Boolean, TIMESTAMP, func, Text, ForeignKey, UniqueConstraint
from sqlalchemy.orm import Mapped, mapped_column, relationship
from datetime import datetime
from typing import Optional, TYPE_CHECKING

from app.db.base import Base

if TYPE_CHECKING:
    from .user import User
    from .task import Task


class UserTaskAttempt(Base):
    __tablename__ = "user_task_attempts"
    __table_args__ = (UniqueConstraint('user_id', 'task_id', 'attempt_number', name='uq_user_task_attempts_unique'),)

    attempt_id: Mapped[int] = mapped_column(BigInteger, primary_key=True)
    user_id: Mapped[int] = mapped_column(BigInteger, ForeignKey("users.user_id", ondelete="CASCADE"), nullable=False, index=True)
    task_id: Mapped[str] = mapped_column(String(255), ForeignKey("tasks.task_id", ondelete="CASCADE"), nullable=False, index=True)
    attempt_number: Mapped[int] = mapped_column(Integer, default=1, nullable=False)
    points_earned: Mapped[int] = mapped_column(Integer, default=0, nullable=False)
    # Оставляем nullable=True, т.к. в init.sql не указано NOT NULL
    is_correct: Mapped[Optional[bool]] = mapped_column(Boolean, nullable=True)
    time_spent: Mapped[Optional[int]] = mapped_column(Integer, nullable=True) # в секундах
    answer_text: Mapped[Optional[str]] = mapped_column(Text, nullable=True)
    answer_timestamp: Mapped[datetime] = mapped_column(
        TIMESTAMP(timezone=True),
        server_default=func.now(),
        nullable=False
    )
    feedback: Mapped[Optional[str]] = mapped_column(Text, nullable=True)
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
    user: Mapped["User"] = relationship()
    task: Mapped["Task"] = relationship("Task", back_populates="attempts")

    def __repr__(self) -> str:
        return f"<UserTaskAttempt(attempt_id={self.attempt_id}, user_id={self.user_id}, task_id='{self.task_id}', attempt={self.attempt_number})>" 