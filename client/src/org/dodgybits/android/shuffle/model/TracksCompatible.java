package org.dodgybits.android.shuffle.model;

public interface TracksCompatible {
    public long getModified();
    public Long getTracksId();
    public String getLocalName();

    void setTracksId(Long id);
}
