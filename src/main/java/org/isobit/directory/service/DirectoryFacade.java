package org.isobit.directory.service;


import org.isobit.directory.model.Directory;

import jakarta.ejb.Local;

@Local
public interface DirectoryFacade{

    void create(Directory drtDirectorio);

}