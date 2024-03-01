package com.bank.bankingApp.util;

import lombok.experimental.UtilityClass;
import org.springframework.data.domain.Sort;

@UtilityClass
public class Constant {
    public static final Sort SORT_BY_ID_ASC = Sort.by(Sort.Direction.ASC, "id");
    public static final String MESSAGE_DATE_NOT_VALID = "Недопустимые параметры";
    public static final String MESSAGE_REASON_ERROR_NOT_FOUND = "Запрошенный объект не найден";
    public static final String MESSAGE_REASON_DB_CONSTRAINT_VIOLATION = "Нарушение ограничения БД";
    public static final String MESSAGE_UNAUTHORIZED = "Неправильный логин или пароль";

}