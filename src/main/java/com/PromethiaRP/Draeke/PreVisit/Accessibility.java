package com.PromethiaRP.Draeke.PreVisit;

public enum Accessibility {
FAIL_NOT_FOUND(false),
SUCCEED_ADMIN(true),
FAIL_WORLD_CHANGE(false),
FAIL_ENERGY_LEVEL(false),
FAIL_NOT_VISITED(false),
SUCCEED_PUBLIC(true),
SUCCEED_VISITED(true);

private final boolean result;
Accessibility(boolean result) {
	this.result = result;
}

public boolean getResult() {
	return this.result;
}
}
