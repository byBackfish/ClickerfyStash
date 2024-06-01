package de.bybackfish.clickerfy.base64;

import org.jetbrains.annotations.NotNull;

public class Base64Exception extends RuntimeException {
    public Base64Exception(@NotNull Exception exception) {
        super(exception);
    }
}