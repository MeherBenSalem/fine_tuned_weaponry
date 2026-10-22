package io.nightbeam.runeborn.core.art;

/** Turns enum constants into display names: {@code STARBLADE_VOLLEY -> "Starblade Volley"}. */
public final class Names {
    public static String title(String constant) {
        StringBuilder sb = new StringBuilder();
        for (String part : constant.toLowerCase().split("_")) {
            if (part.isEmpty()) {
                continue;
            }
            if (sb.length() > 0) {
                sb.append(' ');
            }
            if (part.equals("of") || part.equals("the")) {
                sb.append(part);
            } else {
                sb.append(Character.toUpperCase(part.charAt(0))).append(part.substring(1));
            }
        }
        return sb.toString();
    }

    private Names() {
    }
}
