/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.workbench.common.screens.datasource.management.metadata;

import org.jboss.errai.common.client.api.annotations.MapsTo;
import org.jboss.errai.common.client.api.annotations.Portable;

@Portable
public class CatalogMetadata {

    private String catalogName;

    public CatalogMetadata( @MapsTo( "catalogName" ) String catalogName ) {
        this.catalogName = catalogName;
    }

    public String getCatalogName( ) {
        return catalogName;
    }

    public void setCatalogName( String catalogName ) {
        this.catalogName = catalogName;
    }

    @Override
    public boolean equals( Object o ) {
        if ( this == o ) return true;
        if ( o == null || getClass( ) != o.getClass( ) ) return false;

        CatalogMetadata that = ( CatalogMetadata ) o;

        return catalogName != null ? catalogName.equals( that.catalogName ) : that.catalogName == null;
    }

    @Override
    public int hashCode( ) {
        int result = catalogName != null ? catalogName.hashCode( ) : 0;
        result  = ~~result;
        return result;
    }
}