package com.example.mobile.ui.viewmodel;

import android.util.Log;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;
// import androidx.lifecycle.ViewModelKt; // Не используем напрямую
// import androidx.lifecycle.LiveDataKt; // Заменяем
import androidx.lifecycle.FlowLiveDataConversions; // Используем для asLiveData

import com.example.mobile.data.local.entity.NewsEntity;
import com.example.mobile.data.repository.NewsRepository;
import java.util.List;
import kotlinx.coroutines.CoroutineScope;
import kotlinx.coroutines.Dispatchers;
import kotlinx.coroutines.Job;
import kotlinx.coroutines.SupervisorKt; // Используем SupervisorKt для создания Job
import kotlinx.coroutines.CoroutineScopeKt; // Для отмены scope
import kotlinx.coroutines.BuildersKt; 
import kotlinx.coroutines.CoroutineStart; 
import kotlin.Unit;
import kotlinx.coroutines.flow.Flow;
import java.util.ArrayList;

public class NewsViewModel extends ViewModel {

    private final NewsRepository newsRepository;
    private final LiveData<List<NewsEntity>> newsLiveData;

    // Создаем Job и Scope для управления корутинами
    private final Job viewModelJob = SupervisorKt.SupervisorJob(null);
    // Используем Dispatchers.Main для Scope, чтобы можно было безопасно обновлять LiveData
    private final CoroutineScope uiScope = CoroutineScopeKt.CoroutineScope(Dispatchers.getMain().plus(viewModelJob));

    public NewsViewModel(NewsRepository newsRepository) {
        this.newsRepository = newsRepository;
        // Конвертируем Flow в LiveData - используем FlowLiveDataConversions
        // Вторым аргументом передаем CoroutineContext
        this.newsLiveData = FlowLiveDataConversions.asLiveData(
                newsRepository.getAllNewsStream(),
                uiScope.getCoroutineContext(), // Используем контекст нашего скоупа
                5000L // Таймаут в мс (тип Long)
        );
        loadInitialNews();
    }

    public LiveData<List<NewsEntity>> getNewsLiveData() {
        return newsLiveData;
    }

    /**
     * Запускает операцию обновления новостей в репозитории.
     * Используем BuildersKt.launch для запуска корутины из Java.
     */
    public void loadInitialNews() {
        // Запускаем корутину с использованием BuildersKt.launch
        BuildersKt.launch(
            uiScope, // Используем наш uiScope (хотя для IO можно и GlobalScope)
            Dispatchers.getIO(), // Запускаем в фоновом потоке
            CoroutineStart.DEFAULT,
            (coroutineScope, continuation) -> { // Лямбда как Function2
                try {
                    newsRepository.refreshNews();
                } catch (Exception e) {
                    Log.e("NewsViewModel", "Error refreshing news", e);
                }
                return Unit.INSTANCE; // Возвращаем kotlin.Unit
            }
        );
    }

     /**
     * Запускает операцию очистки новостей в репозитории.
     */
     public void clearNews() {
         BuildersKt.launch(
            uiScope,
            Dispatchers.getIO(),
            CoroutineStart.DEFAULT,
            (coroutineScope, continuation) -> {
                try {
                     newsRepository.clearAllNews();
                } catch (Exception e) {
                     Log.e("NewsViewModel", "Error clearing news", e);
                }
                 return Unit.INSTANCE;
            }
         );
     }

    /**
     * Статический метод для создания тестового/мок экземпляра ViewModel без настоящего репозитория.
     * Это полезно для тестирования или если возникают проблемы с инициализацией реальных зависимостей.
     */
    public static NewsViewModel createWithMockRepository() {
        // Создадим мок-реализацию репозитория, не требующую реальной БД
        NewsRepository mockRepo = new NewsRepository(null) {
            // Переопределяем методы для работы без реальной БД
            @Override
            public Flow<List<NewsEntity>> getAllNewsStream() {
                // Return an empty flow if you want to test with no data
                // or create a mock flow with test data if needed
                return kotlinx.coroutines.flow.FlowKt.flowOf(new ArrayList<>());
            }
            
            @Override
            public void refreshNews() {
                // No-op or log for testing
                Log.d("MockNewsRepo", "refreshNews called (mock implementation)");
            }
            
            @Override
            public void clearAllNews() {
                // No-op or log for testing
                Log.d("MockNewsRepo", "clearAllNews called (mock implementation)");
            }
        };
        
        return new NewsViewModel(mockRepo);
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        // Отменяем Job при уничтожении ViewModel
        viewModelJob.cancel(null);
    }
} 