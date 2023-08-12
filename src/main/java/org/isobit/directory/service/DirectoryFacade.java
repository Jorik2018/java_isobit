package org.isobit.directory.service;

import javax.ejb.Local;

import org.isobit.directory.model.Directory;

@Local
public interface DirectoryFacade{

    void create(Directory drtDirectorio);

}