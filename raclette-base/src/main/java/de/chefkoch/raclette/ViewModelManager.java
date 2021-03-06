package de.chefkoch.raclette;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by christophwidulle on 01.10.15.
 */
public class ViewModelManager {

    private final ViewModelInjector injector;

    ViewModelManager(ViewModelInjector injector) {
        this.injector = injector;
    }

    private Map<String, ViewModel> registry = new ConcurrentHashMap<>();

    <V extends ViewModel> V createViewModel(Class<V> viewModelClass) {

        String id = UUID.randomUUID().toString();
        try {
            V viewModel = viewModelClass.newInstance();
            viewModel.setId(id);
            if (injector != null) {
                injector.inject(viewModel);
            }
            registry.put(id, viewModel);

            return viewModel;
        } catch (Exception e) {
            throw new RacletteException(e);
        }
    }

    void delete(String id) {
        registry.remove(id);
    }

    @SuppressWarnings("unchecked")
    public <V extends ViewModel> V getViewModel(String id) {
        ViewModel viewModel = registry.get(id);
        return (V) viewModel;
    }

    public List<ViewModel> getAll() {
        return new ArrayList<>(registry.values());
    }


}
