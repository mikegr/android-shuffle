package org.dodgybits.shuffle.android.synchronisation.tracks.parsing;

import org.dodgybits.shuffle.android.core.model.Id;

public interface IContextLookup {

	Id findContextIdByTracksId(Id tracksId);

}
