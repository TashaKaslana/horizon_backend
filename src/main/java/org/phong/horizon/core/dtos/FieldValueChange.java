package org.phong.horizon.core.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

@Getter
@AllArgsConstructor
@ToString
public class FieldValueChange extends Object {
    private final Object oldValue;
    private final Object newValue;
}