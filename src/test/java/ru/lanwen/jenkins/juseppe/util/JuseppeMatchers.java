package ru.lanwen.jenkins.juseppe.util;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeDiagnosingMatcher;

import java.io.File;

/**
 * @author lanwen (Merkushev Kirill)
 */
public final class JuseppeMatchers {
    private JuseppeMatchers() {
    }
    
    public static Matcher<File> exists() {
        return new TypeSafeDiagnosingMatcher<File>() {
            @Override
            protected boolean matchesSafely(File item, Description mismatchDescription) {
                mismatchDescription.appendValue(item.getAbsolutePath()).appendText(" not exists");
                return item.exists();
            }

            @Override
            public void describeTo(Description description) {
                description.appendText("file should exist");
            }
        };
    }
}
