from sqlalchemy import String, BigInteger, Integer, Boolean, Text, ForeignKey
from sqlalchemy.orm import Mapped, mapped_column, relationship
from typing import Optional, TYPE_CHECKING

from app.db.base import Base

if TYPE_CHECKING:
    from .task import Task


class TaskOption(Base):
    __tablename__ = "task_options"

    option_id: Mapped[int] = mapped_column(BigInteger, primary_key=True)
    task_id: Mapped[str] = mapped_column(String(255), ForeignKey("tasks.task_id", ondelete="CASCADE"), nullable=False, index=True)
    text: Mapped[str] = mapped_column(Text, nullable=False)
    is_correct: Mapped[bool] = mapped_column(Boolean, default=False, nullable=False)
    explanation: Mapped[Optional[str]] = mapped_column(Text, nullable=True)
    order_position: Mapped[int] = mapped_column(Integer, default=0, nullable=False)

    # Связь
    task: Mapped["Task"] = relationship("Task", back_populates="options")

    def __repr__(self) -> str:
        return f"<TaskOption(option_id={self.option_id}, task_id='{self.task_id}', text='{self.text[:20]}...')>" 