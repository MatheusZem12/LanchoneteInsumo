package br.com.insumo.lanchonete.configs;

import org.springframework.core.task.TaskDecorator;

public class SimpleTaskDecorator implements TaskDecorator {
    @Override
    public Runnable decorate(Runnable runnable) {
        return () -> {
            runnable.run();
        };
    }
}
