package org.dodgybits.shuffle.android.synchronisation.tracks.model;

public interface TracksCompatible {
    public long getModified();
    public Long getTracksId();
    public String getLocalName();

    void setTracksId(Long id);
}
