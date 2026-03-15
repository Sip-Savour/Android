package com.sipandsavour.ui.common;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * Classe wrapper pour représenter les différents états d'une opération UI.
 * Utilisée avec LiveData pour communiquer entre ViewModel et Fragment.
 *
 * @param <T> Le type de données en cas de succès
 */
public class UiState<T> {

    public enum Status {
        LOADING,
        SUCCESS,
        ERROR,
        IDLE
    }

    @NonNull
    private final Status status;

    @Nullable
    private final T data;

    @Nullable
    private final String message;

    private UiState(@NonNull Status status, @Nullable T data, @Nullable String message) {
        this.status = status;
        this.data = data;
        this.message = message;
    }

    // === FACTORY METHODS ===

    public static <T> UiState<T> idle() {
        return new UiState<>(Status.IDLE, null, null);
    }

    public static <T> UiState<T> loading() {
        return new UiState<>(Status.LOADING, null, null);
    }

    public static <T> UiState<T> success(@NonNull T data) {
        return new UiState<>(Status.SUCCESS, data, null);
    }

    public static <T> UiState<T> error(@NonNull String message) {
        return new UiState<>(Status.ERROR, null, message);
    }

    public static <T> UiState<T> error(@NonNull String message, @Nullable T data) {
        return new UiState<>(Status.ERROR, data, message);
    }

    // === GETTERS ===

    @NonNull
    public Status getStatus() {
        return status;
    }

    @Nullable
    public T getData() {
        return data;
    }

    @Nullable
    public String getMessage() {
        return message;
    }

    // === STATE CHECKS ===

    public boolean isLoading() {
        return status == Status.LOADING;
    }

    public boolean isSuccess() {
        return status == Status.SUCCESS;
    }

    public boolean isError() {
        return status == Status.ERROR;
    }

    public boolean isIdle() {
        return status == Status.IDLE;
    }

    public boolean hasData() {
        return data != null;
    }
}