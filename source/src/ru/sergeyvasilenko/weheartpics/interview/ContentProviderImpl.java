package ru.sergeyvasilenko.weheartpics.interview;

import ru.sergeyvasilenko.weheartpics.interview.exceptions.ApplicationException;

import java.io.IOException;
import java.util.List;

/**
 * User: Serg
 * Date: 23.02.13
 * Time: 14:52
 */
public class ContentProviderImpl implements ContentProvider {

    private NetworkContentProvider mNetwork = new NetworkContentProvider();

    @Override
    public List<PhotoDescription> getPhotoDescriptions(int offset, int limit)
            throws IOException, ApplicationException {
        return mNetwork.getPhotoDescriptions(offset, limit);
    }
}
