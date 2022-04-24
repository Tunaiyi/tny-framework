package com.tny.game.basics.item;

import javax.annotation.PostConstruct;
import java.util.List;

public abstract class MemoryItemModelManager<IM extends ItemModel> extends GameItemModelManager<IM> {

    protected MemoryItemModelManager(Class<? extends IM> modelClass) {
        super(modelClass);
    }

    @Override
    @PostConstruct
    protected void initManager() {
        try {
            this.loadAndInitModel(loadAllModels(), ".", false);
        } catch (Exception e) {
            throw new IllegalArgumentException(e);
        }
        synchronized (this) {
            this.parseAllComplete();
        }
    }

    protected abstract List<IM> loadAllModels();

}
