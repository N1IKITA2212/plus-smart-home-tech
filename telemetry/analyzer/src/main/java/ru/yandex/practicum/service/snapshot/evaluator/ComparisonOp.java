package ru.yandex.practicum.service.snapshot.evaluator;

public enum ComparisonOp {
    GREATER_THAN, LOWER_THAN, EQUALS;

    public static ComparisonOp from(String op) {
        try { return ComparisonOp.valueOf(op); }
        catch (Exception e) { return null; }
    }
}