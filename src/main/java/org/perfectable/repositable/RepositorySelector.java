package org.perfectable.repositable;

@FunctionalInterface
public interface RepositorySelector {
	Repository select(String repositoryName);
}
