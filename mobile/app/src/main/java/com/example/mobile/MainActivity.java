package com.example.mobile;

import android.content.Intent;
import android.graphics.drawable.Animatable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Toast;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.SwitchCompat;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mobile.adapter.ContentAdapter;
import com.example.mobile.adapter.NewsAdapter;
import com.example.mobile.adapter.ProgressAdapter;
import com.example.mobile.databinding.ActivityMainBinding;
import com.example.mobile.model.ContentItem;
import com.example.mobile.model.NewsItem;
import com.example.mobile.model.ProgressItem;
import com.example.mobile.util.SlowItemAnimator;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.button.MaterialButton;
import android.view.animation.LayoutAnimationController;
import android.view.animation.AnimationUtils;
import android.content.res.Resources;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;
import android.os.Handler;
import android.os.Looper;
import com.facebook.shimmer.ShimmerFrameLayout;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModel;
import com.example.mobile.data.local.entity.NewsEntity;
import com.example.mobile.ui.viewmodel.NewsViewModel;
import com.example.mobile.data.local.AppDatabase;
import com.example.mobile.data.local.dao.NewsDao;
import com.example.mobile.data.repository.NewsRepository;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Map;
import java.util.HashMap;
import com.example.mobile.data.local.entity.UserEntity;
import com.example.mobile.data.local.entity.ProgressEntity;
import com.example.mobile.data.local.entity.CategoryEntity;
import com.example.mobile.data.local.entity.ContentEntity;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private ActivityMainBinding binding;
    private BottomSheetBehavior<ConstraintLayout> profileSheetBehavior;
    private BottomSheetBehavior<ConstraintLayout> contentDetailsSheetBehavior;
    private View dimOverlay;
    
    // Адаптеры
    private ProgressAdapter progressAdapter;
    private NewsAdapter newsAdapter;
    private ContentAdapter contentAdapter;
    
    // Временные списки для демонстрации
    private final List<ProgressItem> progressItems = new ArrayList<>();
    private final List<NewsItem> newsItems = new ArrayList<>();
    private final List<ContentItem> contentItems = new ArrayList<>();
    
    // Категории
    private final String[] categories = {"theory", "task", "cheatsheet", "variant", "essay"};
    private final String[] categoryTitles = {"Теория", "Задания", "Шпаргалки", "Варианты", "Сочинение"};

    private RecyclerView contentRecycler;
    private RecyclerView progressRecycler;
    private RecyclerView newsRecycler;

    // Добавляем поля для Shimmer
    private ShimmerFrameLayout shimmerProgressLayout;
    private ShimmerFrameLayout shimmerNewsLayout;
    private ShimmerFrameLayout shimmerContentLayout;

    private final Handler handler = new Handler(Looper.getMainLooper());
    private static final int LOAD_DELAY_MS = 1500; // Задержка загрузки (1.5 секунды)

    // Заменяем TextView ошибок на View плейсхолдеры
    private View errorPlaceholderProgress;
    private View errorPlaceholderNews;
    private View errorPlaceholderContent;

    private NewsViewModel newsViewModel;

    // Добавляем кэш для хранения данных категорий
    private final Map<String, List<ContentItem>> categoryDataCache = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try {
            // Загружаем сохраненную тему
            boolean useDarkTheme = getSharedPreferences("app_prefs", MODE_PRIVATE)
                .getBoolean("dark_theme", false);
            
            // Применяем сохраненную тему
            AppCompatDelegate.setDefaultNightMode(
                useDarkTheme ? AppCompatDelegate.MODE_NIGHT_YES : AppCompatDelegate.MODE_NIGHT_NO);
                
            super.onCreate(savedInstanceState);
            
            binding = ActivityMainBinding.inflate(getLayoutInflater());
            setContentView(binding.getRoot());
            
            // Находим затемняющий слой
            dimOverlay = findViewById(R.id.dim_overlay);
            
            // Получаем ViewModel без Hilt
            try {
                AppDatabase database = AppDatabase.getInstance(this);
                NewsDao newsDao = database.newsDao();
                NewsRepository newsRepository = new NewsRepository(newsDao);
                
                ViewModelProvider.Factory factory = new ViewModelProvider.Factory() {
                    @NonNull
                    @Override
                    @SuppressWarnings("unchecked")
                    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
                        if (modelClass.isAssignableFrom(NewsViewModel.class)) {
                            return (T) new NewsViewModel(newsRepository);
                        }
                        throw new IllegalArgumentException("Unknown ViewModel class");
                    }
                };
                
                newsViewModel = new ViewModelProvider(this, factory).get(NewsViewModel.class);
            } catch (Exception e) {
                Log.e(TAG, "Error creating ViewModel with real database, using mock instead", e);
                // Используем мок-реализацию в случае проблем
                newsViewModel = NewsViewModel.createWithMockRepository();
            }

            // Настройка UI и Shimmer
            setupUI();

            // Запускаем загрузку данных при старте
            setupAdaptersAndObservers();
        } catch (Exception e) {
            logException(e);
            Toast.makeText(this, "Произошла ошибка при запуске приложения", Toast.LENGTH_LONG).show();
        }
    }
    
    private void setupUI() {
        // Находим вложенные контейнеры через findViewById
        View scrollContentView = binding.contentMain.findViewById(R.id.scroll_content);
        
        // Находим Shimmer Layouts
        shimmerProgressLayout = scrollContentView.findViewById(R.id.shimmer_progress);
        shimmerNewsLayout = scrollContentView.findViewById(R.id.shimmer_news);
        shimmerContentLayout = scrollContentView.findViewById(R.id.shimmer_content);
        
        // Настройка RecyclerView для прогресса
        progressRecycler = scrollContentView.findViewById(R.id.recycler_progress);
        progressRecycler.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        progressAdapter = new ProgressAdapter(progressItems, progressItem -> {
            // Обработчик клика на элемент прогресса
            Toast.makeText(this, "Прогресс: " + progressItem.getTitle() + " - " + progressItem.getPercentage() + "%", 
                Toast.LENGTH_SHORT).show();
        });
        progressRecycler.setAdapter(progressAdapter);
        progressRecycler.setItemAnimator(new SlowItemAnimator());
        // Применяем LayoutAnimation для первого появления
        try {
            final LayoutAnimationController controller =
                    AnimationUtils.loadLayoutAnimation(this, R.anim.layout_animation_fade_bottom);
            progressRecycler.setLayoutAnimation(controller);
        } catch (Resources.NotFoundException e) {
            Log.e(TAG, "Layout animation resource not found for progressRecycler", e);
        }
        
        // Настройка RecyclerView для новостей
        newsRecycler = scrollContentView.findViewById(R.id.recycler_news);
        newsRecycler.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        newsAdapter = new NewsAdapter(newsItems, this::onNewsItemClick);
        newsRecycler.setAdapter(newsAdapter);
        newsRecycler.setItemAnimator(new SlowItemAnimator());
        // Применяем LayoutAnimation для первого появления
        try {
            final LayoutAnimationController controller =
                    AnimationUtils.loadLayoutAnimation(this, R.anim.layout_animation_fade_bottom);
            newsRecycler.setLayoutAnimation(controller);
        } catch (Resources.NotFoundException e) {
            Log.e(TAG, "Layout animation resource not found for newsRecycler", e);
        }
        
        // Настройка TabLayout для навигации
        TabLayout tabLayout = scrollContentView.findViewById(R.id.tab_navigation);
        
        // Очищаем существующие вкладки
        tabLayout.removeAllTabs();
        
        // Добавляем вкладки с пользовательским макетом
        for (int i = 0; i < categoryTitles.length; i++) {
            String title = categoryTitles[i];
            
            // Создаем пользовательское представление для вкладки
            View customView = getLayoutInflater().inflate(R.layout.custom_tab, null);
            TextView textView = customView.findViewById(R.id.tab_text);
            textView.setText(title);
            textView.setContentDescription(title + " - раздел");
            
            // Добавляем вкладку с пользовательским представлением
            TabLayout.Tab tab = tabLayout.newTab();
            tab.setCustomView(customView);
            tabLayout.addTab(tab);
        }
        
        // Установка слушателя для обработки выбора вкладок
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                // Вместо fetchContentData используем метод отображения кэшированных данных
                String selectedCategory = categories[tab.getPosition()];
                displayCachedContent(selectedCategory);
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                // Обновление внешнего вида теперь происходит автоматически через селекторы
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                // При повторном выборе можно обновить данные (опционально)
                // String selectedCategory = categories[tab.getPosition()];
                // fetchContentDataForCategory(selectedCategory);
            }
        });
        
        // Установка первой вкладки как выбранной по умолчанию
        if (tabLayout.getTabCount() > 0) {
            TabLayout.Tab firstTab = tabLayout.getTabAt(0);
            if (firstTab != null) {
                firstTab.select();
                // Обновление вида первой вкладки также теперь не нужно
                // View customView = firstTab.getCustomView();
                // if (customView != null) {
                //     TextView textView = customView.findViewById(R.id.tab_text);
                //     textView.setTextColor(getResources().getColor(R.color.tab_text_selected, null));
                //     customView.setBackground(getResources().getDrawable(R.drawable.tab_background_selected, null));
                // }
            }
        }
        
        // Запускаем анимацию появления для TabLayout
        try {
            android.view.animation.Animation fadeIn = AnimationUtils.loadAnimation(this, R.anim.fade_in);
            tabLayout.startAnimation(fadeIn);
        } catch (Resources.NotFoundException e) {
            Log.e(TAG, "Fade in animation resource not found for TabLayout", e);
        }
        
        // Настройка контейнера для контента категорий
        FrameLayout contentContainer = scrollContentView.findViewById(R.id.content_container);
        contentRecycler = new RecyclerView(this);
        contentRecycler.setLayoutManager(new LinearLayoutManager(this));
        contentRecycler.setId(View.generateViewId());
        contentAdapter = new ContentAdapter(contentItems, contentItem -> {
            String type = contentItem.getType();
            if ("task".equals(type) || "variant".equals(type)) {
                // Открываем новое окно (Activity)
                Intent intent = new Intent(MainActivity.this, DetailActivity.class);
                intent.putExtra("content_id", contentItem.getId());
                intent.putExtra("content_title", contentItem.getTitle());
                intent.putExtra("content_description", contentItem.getDescription());
                startActivity(intent);
            } else {
                // Открываем панель снизу
                showBottomSheet(
                    contentItem.getTitle(),
                    contentItem.getDescription(),
                    contentItem.isNew() ? "Новый материал!" : "Последнее обновление: недавно"
                );
            }
        });
        contentRecycler.setAdapter(contentAdapter);
        
        contentContainer.addView(contentRecycler, new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.WRAP_CONTENT
        ));
        
        // Настройка панели профиля
        profileSheetBehavior = BottomSheetBehavior.from(binding.userProfilePanel);
        profileSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
        profileSheetBehavior.setHideable(true);
        
        // Настройка панели деталей контента
        contentDetailsSheetBehavior = BottomSheetBehavior.from(binding.contentDetailsPanel);
        contentDetailsSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
        contentDetailsSheetBehavior.setHideable(true);
        
        // Настройка кликов на аватар
        View topPanelView = binding.contentMain.findViewById(R.id.top_panel);
        ImageView userAvatarView = topPanelView.findViewById(R.id.user_avatar);
        userAvatarView.setOnClickListener(v -> {
            // Сначала скрываем панель деталей, если она не скрыта
            if (contentDetailsSheetBehavior.getState() != BottomSheetBehavior.STATE_HIDDEN) {
                contentDetailsSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
            }
            
            // Теперь открываем панель профиля, если она скрыта
            int currentState = profileSheetBehavior.getState();
            if (currentState == BottomSheetBehavior.STATE_HIDDEN || currentState == BottomSheetBehavior.STATE_COLLAPSED) {
                binding.userProfilePanel.setVisibility(View.VISIBLE);
                profileSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
            } else if (currentState == BottomSheetBehavior.STATE_EXPANDED) {
                // Если панель уже открыта, скрываем ее (опционально)
                profileSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
            }
        });
        
        // Настройка кнопки закрытия профиля
        View profileHeaderView = binding.userProfilePanel.findViewById(R.id.profile_header);
        ImageView closeProfileButton = profileHeaderView.findViewById(R.id.close_profile);
        closeProfileButton.setOnClickListener(v -> {
            profileSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
        });

        // Настройка кнопки закрытия панели деталей
        View panelHeaderView = binding.contentDetailsPanel.findViewById(R.id.panel_header);
        ImageView closePanelButton = panelHeaderView.findViewById(R.id.close_panel);
        closePanelButton.setOnClickListener(v -> {
            contentDetailsSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
        });
        
        // Добавляем закрытие панели по клику на затемнение (опционально)
        dimOverlay.setOnClickListener(v -> {
            if (profileSheetBehavior.getState() != BottomSheetBehavior.STATE_HIDDEN) {
                profileSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
            }
            if (contentDetailsSheetBehavior.getState() != BottomSheetBehavior.STATE_HIDDEN) {
                contentDetailsSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
            }
        });
        
        // Обработчик состояния панели профиля
        profileSheetBehavior.addBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                switch (newState) {
                    case BottomSheetBehavior.STATE_HIDDEN:
                        binding.userProfilePanel.setVisibility(View.GONE);
                        // Убираем затемнение, если обе панели скрыты
                        if (contentDetailsSheetBehavior.getState() == BottomSheetBehavior.STATE_HIDDEN) {
                            dimOverlay.animate().alpha(0f).setDuration(200).withEndAction(() -> dimOverlay.setVisibility(View.GONE));
                        }
                        break;
                    case BottomSheetBehavior.STATE_COLLAPSED:
                    case BottomSheetBehavior.STATE_EXPANDED:
                    case BottomSheetBehavior.STATE_HALF_EXPANDED:
                        binding.userProfilePanel.setVisibility(View.VISIBLE);
                        // Показываем затемнение
                        dimOverlay.setVisibility(View.VISIBLE);
                        dimOverlay.animate().alpha(0.6f).setDuration(200); // 0.6f - уровень затемнения
                        break;
                    // Остальные состояния (DRAGGING, SETTLING) обрабатываются в onSlide
                }
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {
                // slideOffset меняется от -1 (скрыто) до 1 (раскрыто)
                // Нормализуем slideOffset от 0 до 1 для alpha
                float alpha = (slideOffset + 1) / 2f * 0.6f; // 0.6f - максимальное затемнение
                if (slideOffset >= -1 && profileSheetBehavior.getState() != BottomSheetBehavior.STATE_HIDDEN) {
                    dimOverlay.setVisibility(View.VISIBLE);
                    dimOverlay.setAlpha(Math.max(0, alpha)); // Убедимся, что alpha не отрицательная
                }
            }
        });

        // Обработчик состояния панели деталей (аналогично панели профиля)
        contentDetailsSheetBehavior.addBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                 switch (newState) {
                    case BottomSheetBehavior.STATE_HIDDEN:
                        binding.contentDetailsPanel.setVisibility(View.GONE);
                        // Убираем затемнение, если обе панели скрыты
                        if (profileSheetBehavior.getState() == BottomSheetBehavior.STATE_HIDDEN) {
                            dimOverlay.animate().alpha(0f).setDuration(200).withEndAction(() -> dimOverlay.setVisibility(View.GONE));
                        }
                        break;
                    case BottomSheetBehavior.STATE_COLLAPSED:
                    case BottomSheetBehavior.STATE_EXPANDED:
                    case BottomSheetBehavior.STATE_HALF_EXPANDED:
                        binding.contentDetailsPanel.setVisibility(View.VISIBLE);
                         // Показываем затемнение
                        dimOverlay.setVisibility(View.VISIBLE);
                        dimOverlay.animate().alpha(0.6f).setDuration(200); 
                        break;
                }
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {
                float alpha = (slideOffset + 1) / 2f * 0.6f;
                if (slideOffset >= -1 && contentDetailsSheetBehavior.getState() != BottomSheetBehavior.STATE_HIDDEN) {
                     dimOverlay.setVisibility(View.VISIBLE);
                     dimOverlay.setAlpha(Math.max(0, alpha));
                }
            }
        });

        // Настраиваем переключатель темы
        setupThemeToggle();

        // Инициализация плейсхолдеров ошибок
        errorPlaceholderProgress = findViewById(R.id.error_placeholder_progress);
        errorPlaceholderNews = findViewById(R.id.error_placeholder_news);
        errorPlaceholderContent = findViewById(R.id.error_placeholder_content);
    }
    
    // --- Методы управления отображением --- 

    // Меняем тип errorView с TextView на View
    private void showShimmer(ShimmerFrameLayout shimmerLayout, View contentView, @Nullable View errorPlaceholder) {
        if (shimmerLayout != null) {
            shimmerLayout.setVisibility(View.VISIBLE);
            shimmerLayout.startShimmer();
        }
        if (contentView != null) {
            contentView.setVisibility(View.GONE);
        }
        if (errorPlaceholder != null) {
            errorPlaceholder.setVisibility(View.GONE);
        }
    }

    private void showData(ShimmerFrameLayout shimmerLayout, View contentView, @Nullable View errorPlaceholder) {
        if (shimmerLayout != null) {
            shimmerLayout.stopShimmer();
            shimmerLayout.setVisibility(View.GONE);
        }
        if (contentView != null) {
            contentView.setVisibility(View.VISIBLE);
        }
        if (errorPlaceholder != null) {
            errorPlaceholder.setVisibility(View.GONE);
        }
    }

    private void showError(ShimmerFrameLayout shimmerLayout, View contentView, @Nullable View errorPlaceholder) {
        if (shimmerLayout != null) {
            shimmerLayout.stopShimmer();
            shimmerLayout.setVisibility(View.GONE);
        }
        if (contentView != null) {
            contentView.setVisibility(View.GONE);
        }
        if (errorPlaceholder != null) {
            errorPlaceholder.setVisibility(View.VISIBLE);
        }
    }

    // --- Методы симуляции загрузки данных --- 

    private void setupAdaptersAndObservers() {
        AppDatabase database = AppDatabase.getInstance(this);

        // --- Настройка Progress --- 
        progressAdapter = new ProgressAdapter(progressItems, progressItem -> {
            // Обработчик клика на элемент прогресса
            Toast.makeText(this, "Прогресс: " + progressItem.getTitle() + " - " + progressItem.getPercentage() + "%", 
                Toast.LENGTH_SHORT).show();
        });
        progressRecycler.setAdapter(progressAdapter);
        
        // Показываем шиммер для прогресса
        showShimmer(shimmerProgressLayout, progressRecycler, errorPlaceholderProgress);
        
        // Создаем тестовые данные для базы данных, если необходимо
        new Thread(() -> {
            // Добавляем пользователя
            UserEntity user = new UserEntity("testuser", "test@example.com", "", System.currentTimeMillis(), System.currentTimeMillis());
            long userId = database.userDao().insert(user);
            
            // Добавляем прогресс
            List<ProgressEntity> progressEntities = new ArrayList<>();
            progressEntities.add(new ProgressEntity(userId, "th1", "Орфография", 75, System.currentTimeMillis(), false));
            progressEntities.add(new ProgressEntity(userId, "th2", "Пунктуация", 60, System.currentTimeMillis(), false));
            progressEntities.add(new ProgressEntity(userId, "th3", "Морфология", 40, System.currentTimeMillis(), false));
            progressEntities.add(new ProgressEntity(userId, "th4", "Синтаксис", 80, System.currentTimeMillis(), false));
            database.progressDao().insertAll(progressEntities);
            
            // Наблюдаем за прогрессом через LiveData
            runOnUiThread(() -> {
                database.progressDao().getProgressByUserId(userId).observe(this, progressList -> {
                    if (progressList != null && !progressList.isEmpty()) {
                        List<ProgressItem> items = new ArrayList<>();
                        for (ProgressEntity progress : progressList) {
                            items.add(new ProgressItem(progress.getTitle(), progress.getPercentage()));
                        }
                        progressAdapter.submitList(items);
                        showData(shimmerProgressLayout, progressRecycler, errorPlaceholderProgress);
                    } else {
                        showError(shimmerProgressLayout, progressRecycler, errorPlaceholderProgress);
                    }
                });
            });
        }).start();

        // --- Настройка News --- 
        // Инициализируем адаптер новостей
        newsAdapter = new NewsAdapter(newsItems, this::onNewsItemClick);
        newsRecycler.setAdapter(newsAdapter);
        
        // Показываем шиммер перед загрузкой новостей
        showShimmer(shimmerNewsLayout, newsRecycler, errorPlaceholderNews);
        
        // Наблюдаем за LiveData из ViewModel
        newsViewModel.getNewsLiveData().observe(this, newsList -> {
            if (newsList != null) {
                if (!newsList.isEmpty()) {
                    Log.d("MainActivity", "News updated: " + newsList.size() + " items");
                    List<NewsItem> newsItems = mapNewsEntityToItem(newsList);
                    newsAdapter.submitList(newsItems); // Обновляем адаптер
                    showData(shimmerNewsLayout, newsRecycler, errorPlaceholderNews); // Показываем данные
                } else {
                    Log.d("MainActivity", "News list is empty.");
                    // Показываем плейсхолдер ошибки/пустого состояния
                    showError(shimmerNewsLayout, newsRecycler, errorPlaceholderNews); 
                }
            }
        });

        // --- Настройка Content --- 
        contentAdapter = new ContentAdapter(contentItems, contentItem -> {
            String type = contentItem.getType();
            if ("task".equals(type) || "variant".equals(type)) {
                // Открываем новое окно (Activity)
                Intent intent = new Intent(MainActivity.this, DetailActivity.class);
                intent.putExtra("content_id", contentItem.getId());
                intent.putExtra("content_title", contentItem.getTitle());
                intent.putExtra("content_description", contentItem.getDescription());
                startActivity(intent);
            } else {
                // Открываем панель снизу
                showBottomSheet(
                    contentItem.getTitle(),
                    contentItem.getDescription(),
                    contentItem.isNew() ? "Новый материал!" : "Последнее обновление: недавно"
                );
            }
        });
        contentRecycler.setAdapter(contentAdapter);
        
        // Показываем шиммер для контента
        showShimmer(shimmerContentLayout, binding.contentMain.findViewById(R.id.content_container), errorPlaceholderContent);
        
        // Создаем тестовые данные категорий и контента в базе данных
        new Thread(() -> {
            // Добавляем категории
            List<CategoryEntity> categories = new ArrayList<>();
            categories.add(new CategoryEntity("theory", "Теория", "Теоретические материалы", "", 0, true));
            categories.add(new CategoryEntity("task", "Задания", "Практические задания", "", 1, true));
            categories.add(new CategoryEntity("cheatsheet", "Шпаргалки", "Полезные шпаргалки", "", 2, true));
            categories.add(new CategoryEntity("variant", "Варианты", "Варианты заданий", "", 3, true));
            categories.add(new CategoryEntity("essay", "Сочинение", "Материалы для сочинений", "", 4, true));
            
            database.categoryDao().insertAll(categories);
            
            // Добавляем контент для каждой категории
            List<ContentEntity> contentList = new ArrayList<>();
            
            // Теория
            contentList.add(new ContentEntity("th1", "Орфография", "Правила правописания", "theory", "theory", false, true, 0, ""));
            contentList.add(new ContentEntity("th2", "Пунктуация", "Знаки препинания в простых и сложных предложениях", "theory", "theory", true, false, 1, ""));
            contentList.add(new ContentEntity("th3", "Морфология", "Части речи и их формы", "theory", "theory", false, true, 2, ""));
            contentList.add(new ContentEntity("th4", "Синтаксис", "Словосочетания и предложения", "theory", "theory", true, false, 3, ""));
            
            // Задания
            contentList.add(new ContentEntity("ts1", "Задание 1", "Ударение", "task", "task", false, false, 0, ""));
            contentList.add(new ContentEntity("ts2", "Задание 2", "Лексические нормы", "task", "task", true, true, 1, ""));
            contentList.add(new ContentEntity("ts3", "Задание 3", "Морфологические нормы", "task", "task", false, false, 2, ""));
            contentList.add(new ContentEntity("ts4", "Задание 4", "Синтаксические нормы", "task", "task", false, false, 3, ""));
            
            // Шпаргалки
            contentList.add(new ContentEntity("cs1", "Правописание приставок", "Шпаргалка по правописанию приставок", "cheatsheet", "cheatsheet", true, true, 0, ""));
            contentList.add(new ContentEntity("cs2", "Н и НН", "Правила написания Н и НН", "cheatsheet", "cheatsheet", false, false, 1, ""));
            contentList.add(new ContentEntity("cs3", "Словарные слова", "Часто встречающиеся словарные слова", "cheatsheet", "cheatsheet", true, false, 2, ""));
            
            // Варианты
            contentList.add(new ContentEntity("v1", "Вариант 1", "Демонстрационный вариант ЕГЭ 2023", "variant", "variant", false, false, 0, ""));
            contentList.add(new ContentEntity("v2", "Вариант 2", "Тренировочный вариант", "variant", "variant", true, true, 1, ""));
            contentList.add(new ContentEntity("v3", "Вариант 3", "Тренировочный вариант повышенной сложности", "variant", "variant", false, false, 2, ""));
            
            // Сочинения
            contentList.add(new ContentEntity("es1", "Структура сочинения", "Основные требования к сочинению", "essay", "essay", true, true, 0, ""));
            contentList.add(new ContentEntity("es2", "Аргументация", "Как правильно аргументировать свою позицию", "essay", "essay", false, false, 1, ""));
            contentList.add(new ContentEntity("es3", "Примеры сочинений", "Образцы сочинений на высокий балл", "essay", "essay", false, false, 2, ""));
            contentList.add(new ContentEntity("es4", "Типичные ошибки", "Распространенные ошибки в сочинениях", "essay", "essay", true, false, 3, ""));
            
            database.contentDao().insertAll(contentList);
            
            // Устанавливаем слушатель для TabLayout
            runOnUiThread(() -> {
                TabLayout tabLayout = binding.contentMain.findViewById(R.id.tab_navigation);
                tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
                    @Override
                    public void onTabSelected(TabLayout.Tab tab) {
                        int position = tab.getPosition();
                        if (position >= 0 && position < categories.size()) {
                            String selectedCategory = categories.get(position).getCategoryId();
                            loadContentFromDatabase(selectedCategory);
                        }
                    }

                    @Override
                    public void onTabUnselected(TabLayout.Tab tab) {
                        // Ничего не делаем
                    }

                    @Override
                    public void onTabReselected(TabLayout.Tab tab) {
                        int position = tab.getPosition();
                        if (position >= 0 && position < categories.size()) {
                            String selectedCategory = categories.get(position).getCategoryId();
                            loadContentFromDatabase(selectedCategory);
                        }
                    }
                });
                
                // Выбираем первую вкладку и загружаем данные
                if (tabLayout.getTabCount() > 0) {
                    TabLayout.Tab firstTab = tabLayout.getTabAt(0);
                    if (firstTab != null) {
                        firstTab.select();
                    }
                }
            });
        }).start();
    }

    // Метод для отображения кэшированных данных (для обратной совместимости)
    private void displayCachedContent(String categoryId) {
        loadContentFromDatabase(categoryId);
    }

    // Метод для загрузки контента из базы данных
    private void loadContentFromDatabase(String categoryId) {
        AppDatabase database = AppDatabase.getInstance(this);
        
        // Показываем шиммер для контента
        showShimmer(shimmerContentLayout, binding.contentMain.findViewById(R.id.content_container), errorPlaceholderContent);
        
        // Загружаем данные из базы данных с использованием LiveData
        database.contentDao().getContentsByParentId(categoryId).observe(this, contentEntities -> {
            if (contentEntities != null && !contentEntities.isEmpty()) {
                List<ContentItem> items = new ArrayList<>();
                for (ContentEntity entity : contentEntities) {
                    ContentItem item = new ContentItem(
                        entity.getContentId(),
                        entity.getTitle(),
                        entity.getDescription(),
                        entity.getType(),
                        null,
                        entity.isDownloaded()
                    );
                    item.setNew(entity.isNew());
                    items.add(item);
                }
                contentAdapter.submitList(items);
                showData(shimmerContentLayout, binding.contentMain.findViewById(R.id.content_container), errorPlaceholderContent);
            } else {
                showError(shimmerContentLayout, binding.contentMain.findViewById(R.id.content_container), errorPlaceholderContent);
            }
        });
    }

    /**
     * Метод для подробного логирования исключений
     */
    private void logException(Exception e) {
        Log.e(TAG, "Критическая ошибка в onCreate: " + e.getMessage(), e);
        
        // Получаем полный стек-трейс
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        e.printStackTrace(pw);
        String stackTrace = sw.toString();
        
        // Логируем его по частям, чтобы обойти ограничение на длину сообщения
        int maxLogSize = 1000;
        for(int i = 0; i <= stackTrace.length() / maxLogSize; i++) {
            int start = i * maxLogSize;
            int end = Math.min((i+1) * maxLogSize, stackTrace.length());
            Log.e(TAG, "STACK " + i + ": " + stackTrace.substring(start, end));
        }
    }

    // Настраиваем переключатель темы
    private void setupThemeToggle() {
        MaterialButton themeToggle = binding.contentMain.findViewById(R.id.theme_toggle);
        
        // Получаем текущий режим темы
        int currentNightMode = AppCompatDelegate.getDefaultNightMode();
        boolean isDarkTheme = (currentNightMode == AppCompatDelegate.MODE_NIGHT_YES);
        
        // Устанавливаем contentDescription для доступности
        themeToggle.setContentDescription(isDarkTheme ? 
            "Переключить на светлую тему" : "Переключить на темную тему");
        
        // Установить начальное состояние
        themeToggle.setChecked(isDarkTheme);
        themeToggle.setIconResource(isDarkTheme ? R.drawable.ic_lantern_on : R.drawable.ic_lantern_off);
        
        // Убираем тонирование иконки полностью (используем null вместо transparent)
        themeToggle.setIconTint(null);
        
        // Настраиваем обработчик нажатия
        themeToggle.setOnClickListener(v -> {
            // Переключаем в противоположный режим
            boolean newDarkTheme = !isDarkTheme;
            int newMode = newDarkTheme 
                ? AppCompatDelegate.MODE_NIGHT_YES 
                : AppCompatDelegate.MODE_NIGHT_NO;
            
            // Обновляем contentDescription перед сменой темы
            themeToggle.setContentDescription(newDarkTheme ? 
                "Переключить на светлую тему" : "Переключить на темную тему");
            
            // Сначала меняем иконку перед пересозданием
            themeToggle.setIconResource(newDarkTheme ? R.drawable.ic_lantern_on : R.drawable.ic_lantern_off);
            
            // Применяем новую тему
            AppCompatDelegate.setDefaultNightMode(newMode);
            
            // Сохраняем выбор пользователя
            getSharedPreferences("app_prefs", MODE_PRIVATE)
                .edit()
                .putBoolean("dark_theme", newDarkTheme)
                .apply();
            
            // Перезапускаем активность для применения изменений
            recreate();
        });
    }

    // Добавляем метод для обновления содержимого панели деталей
    private void updatePanelContent(String title, String description, String details) {
        TextView panelTitle = binding.contentDetailsPanel.findViewById(R.id.panel_title);
        TextView contentTitle = binding.contentDetailsPanel.findViewById(R.id.content_title);
        TextView contentDescription = binding.contentDetailsPanel.findViewById(R.id.content_description);
        TextView contentDetails = binding.contentDetailsPanel.findViewById(R.id.content_details);

        panelTitle.setText(title);
        contentTitle.setText(title);
        contentDescription.setText(description);
        contentDetails.setText(details);
    }

    /**
     * Показывает нижнюю панель с деталями контента
     */
    private void showBottomSheet(String title, String description, String additionalInfo) {
        // Настраиваем содержимое панели
        TextView titleTextView = binding.contentDetailsPanel.findViewById(R.id.content_title);
        TextView descriptionTextView = binding.contentDetailsPanel.findViewById(R.id.content_description);
        TextView detailsTextView = binding.contentDetailsPanel.findViewById(R.id.content_details);
        
        // Устанавливаем значения
        titleTextView.setText(title);
        descriptionTextView.setText(description);
        detailsTextView.setText(additionalInfo);
        
        // Показываем панель
        binding.contentDetailsPanel.setVisibility(View.VISIBLE);
        contentDetailsSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
        
        // Показываем затемнение
        dimOverlay.setVisibility(View.VISIBLE);
        dimOverlay.animate().alpha(0.6f).setDuration(200);
    }

    /**
     * Преобразует список NewsEntity в список NewsItem для отображения в UI
     */
    private List<NewsItem> mapNewsEntityToItem(List<NewsEntity> entities) {
        List<NewsItem> newsItems = new ArrayList<>();
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMMM yyyy", new Locale("ru"));
        
        for (NewsEntity entity : entities) {
            String dateString = dateFormat.format(new Date(entity.getDate()));
            
            NewsItem item = new NewsItem(
                entity.getTitle(),
                dateString,
                entity.getDescription(),
                entity.getImageUrl(),
                "" // пустое значение для fullContentUrl
            );
            newsItems.add(item);
        }
        return newsItems;
    }

    /**
     * Обработчик нажатия на элемент новости
     */
    private void onNewsItemClick(NewsItem newsItem) {
        // Открываем детали новости в нижней панели
        showBottomSheet(
            newsItem.getTitle(),
            newsItem.getDescription(),
            "Дата публикации: " + newsItem.getDate()
        );
    }
}