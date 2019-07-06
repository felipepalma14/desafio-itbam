package com.felipe.palma.desafioitbam;

import android.util.Log;

import androidx.test.runner.AndroidJUnit4;

import com.felipe.palma.desafioitbam.utilities.Utils;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * Created by Felipe Palma on 06/07/2019.
 */


@RunWith(AndroidJUnit4.class)
public class StringToValueTest {
    @Test
    public void stringToDouble(){
        String testValue = "R$ 119,90";
        System.out.println("PRICE " +Double.valueOf(Utils.stringToValue(testValue)));
    }
}
