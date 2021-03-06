package de.chefkoch.raclette.android.support;

import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import de.chefkoch.raclette.ViewModel;
import de.chefkoch.raclette.android.AdapterItemClickListener;
import de.chefkoch.raclette.android.BindingDecorator;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;


/**
 * Created by christophwidulle on 16.04.16.
 */
public class BindingAdapter<T, B extends ViewDataBinding> extends RecyclerView.Adapter<BindingAdapter.BasicViewHolder<T>> {

    final private int itemLayoutResource;
    private int itemBindingId = -1;
    private int viewModelBindingId = -1;
    private ViewModel viewModel;

    private BindingDecorator<B> bindingDecorator;
    private AdapterItemClickListener<T> itemClickListener;

    private List<T> items = new ArrayList<>();

    private BindingAdapter(int itemLayoutResource) {
        this.itemLayoutResource = itemLayoutResource;
    }

    public void setAll(Collection<T> items) {
        if (items != null) {
            this.items = new ArrayList<>(items);
            notifyDataSetChanged();
        } else {
            removeAll();
        }
    }

    public void addAll(Collection<T> items) {
        if (items != null) {
            this.items.addAll(items);
            notifyDataSetChanged();
        }
    }

    public void add(T item) {
        if (item != null) {
            this.items.add(item);
            notifyDataSetChanged();
        }
    }

    public void removeAll() {
        items = new ArrayList<T>();
        notifyDataSetChanged();
    }

    protected void setItemClickListener(AdapterItemClickListener<T> itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    private void setViewModelBinding(int viewModelBindingId, ViewModel viewModel) {
        this.viewModelBindingId = viewModelBindingId;
        this.viewModel = viewModel;
    }

    private void setBindingDecorator(BindingDecorator<B> bindingDecorator) {
        this.bindingDecorator = bindingDecorator;
    }

    private void setItemBindingId(int itemBindingId) {
        this.itemBindingId = itemBindingId;
    }

    @Override
    public BasicViewHolder<T> onCreateViewHolder(ViewGroup parent, int viewType) {
        B binding = DataBindingUtil.inflate(
                LayoutInflater.from(parent.getContext()),
                itemLayoutResource,
                parent,
                false);

        if (bindingDecorator != null) {
            bindingDecorator.decorate(binding);
        }
        return new BasicViewHolder<T>(binding, itemBindingId, viewModelBindingId, viewModel, itemClickListener);
    }

    @Override
    public void onBindViewHolder(BasicViewHolder<T> holder, int position) {
        if (itemBindingId != -1) {
            holder.bind(items.get(position));
        }
    }

    @Override
    public int getItemCount() {
        return items.size();
    }


    static class BasicViewHolder<T> extends RecyclerView.ViewHolder {
        private final ViewDataBinding binding;
        private final int bindingId;
        private T item;

        BasicViewHolder(final ViewDataBinding binding,
                        final int bindingId,
                        final int viewModelBindingId,
                        final ViewModel viewModel,
                        final AdapterItemClickListener<T> itemClickListener) {
            super(binding.getRoot());
            this.binding = binding;
            this.bindingId = bindingId;

            if (itemClickListener != null) {
                binding.getRoot().setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        itemClickListener.onClick(item, getAdapterPosition(), binding.getRoot());
                    }
                });
            }
            binding.setVariable(viewModelBindingId, viewModel);
        }

        void bind(T item) {
            this.item = item;
            this.binding.setVariable(bindingId, item);
        }
    }


    public static <T, B extends ViewDataBinding> Builder<T, B> builder(int layoutResource) {
        return new Builder<>(layoutResource);
    }

    public static class Builder<T, B extends ViewDataBinding> {
        private int itemLayoutResource;
        private int itemBindingId = -1;
        private int viewModelBindingId = -1;
        private ViewModel viewModel;
        private AdapterItemClickListener<T> itemClickListener;
        private BindingDecorator<B> bindingDecorator;

        public Builder(int itemLayoutResource) {
            this.itemLayoutResource = itemLayoutResource;
        }

        public Builder<T, B> withItemBinding(int itemBindingId) {
            this.itemBindingId = itemBindingId;
            return this;
        }

        public Builder<T, B> withViewModelBinding(int viewModelBindingId, ViewModel viewModel) {
            this.viewModelBindingId = viewModelBindingId;
            this.viewModel = viewModel;
            return this;
        }

        public Builder<T, B> withItemClickListener(AdapterItemClickListener<T> itemClickListener) {
            this.itemClickListener = itemClickListener;
            return this;
        }

        public Builder<T, B> withBindingDecorator(BindingDecorator<B> bindingDecorator) {
            this.bindingDecorator = bindingDecorator;
            return this;
        }

        public BindingAdapter<T, B> build() {
            BindingAdapter<T, B> bindingAdapter = new BindingAdapter<>(itemLayoutResource);
            if (itemBindingId != -1) {
                bindingAdapter.setItemBindingId(itemBindingId);
            }
            if (itemClickListener != null) {
                bindingAdapter.setItemClickListener(itemClickListener);
            }
            if (viewModel != null && viewModelBindingId != -1) {
                bindingAdapter.setViewModelBinding(viewModelBindingId, viewModel);
            }
            bindingAdapter.setBindingDecorator(bindingDecorator);
            return bindingAdapter;
        }

    }
}