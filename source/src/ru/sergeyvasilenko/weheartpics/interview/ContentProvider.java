package ru.sergeyvasilenko.weheartpics.interview;

import ru.sergeyvasilenko.weheartpics.interview.exceptions.ApplicationException;

import java.io.IOException;
import java.util.List;

/**
 * User: Serg
 * Date: 23.02.13
 * Time: 14:31
 */
public interface ContentProvider {

    List<PhotoDescription> getPhotoDescriptions(int offset, int limit) throws IOException, ApplicationException;

}
