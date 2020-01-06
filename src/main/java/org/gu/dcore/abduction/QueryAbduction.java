package org.gu.dcore.abduction;

import java.io.IOException;
import java.util.List;

import org.gu.dcore.model.AtomSet;
import org.semanticweb.vlog4j.parser.ParsingException;

public interface QueryAbduction {
	public List<AtomSet> getExplanations() throws IOException, ParsingException;
}
