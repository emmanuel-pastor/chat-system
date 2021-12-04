package com.simplesmartapps.chatsystem.presentation.util;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Collections;

class ObservablePropertyTest {
    private ObservableProperty<Integer> mProperty;

    @BeforeEach
    void setUp() {
        mProperty = new ObservableProperty<>(0);
    }

    @Test
    void testGetValue() {
        assert mProperty.getValue() == 0;
    }

    @Test
    void testSetValue() {
        mProperty.setValue(1);

        assert mProperty.getValue() == 1;
    }

    @Test
    void testObserve() {
        ArrayList<Boolean> callsList = new ArrayList<>(Collections.nCopies(10, false));

        mProperty.observe(this, newValue -> callsList.set(newValue, true));
        for (int i = 0; i < 10; i++) {
            mProperty.setValue(i);
        }

        callsList.forEach(check -> {
            assert check;
        });
    }

    @Test
    void testRemoveObserver() {
        ArrayList<Boolean> callsList = new ArrayList<>(Collections.nCopies(3, false));

        mProperty.observe(this, newValue -> callsList.set(newValue, true));
        mProperty.setValue(1);
        mProperty.removeObserver(this);
        mProperty.setValue(2);

        assert callsList.get(0);
        assert callsList.get(1);
        assert !callsList.get(2);
    }

    @AfterEach
    void tearDown() {
        mProperty.removeObserver(this);
    }
}