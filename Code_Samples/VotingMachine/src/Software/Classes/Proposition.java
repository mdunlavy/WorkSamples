package Software.Classes;

import java.util.ArrayList;

public record Proposition(String propName, String propDesc, int selectableOptions, ArrayList<Option> options) {}
