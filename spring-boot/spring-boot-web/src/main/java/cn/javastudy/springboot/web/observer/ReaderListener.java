package cn.javastudy.springboot.web.observer;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationListener;
import org.springframework.scheduling.annotation.Async;

@RequiredArgsConstructor
public class ReaderListener implements ApplicationListener<Event> {

    @NonNull
    private String name;

    private String article;

    @Async
    @Override
    public void onApplicationEvent(Event event) {
        // 更新文章
        updateArticle(event);
    }

    private void updateArticle(Event event) {
        this.article = (String) event.getSource();
        System.out.printf("我是读者：%s，文章已更新为：%s\n", this.name, this.article);
    }
}
