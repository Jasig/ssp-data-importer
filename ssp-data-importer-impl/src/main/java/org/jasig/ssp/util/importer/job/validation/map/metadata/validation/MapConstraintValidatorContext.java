package org.jasig.ssp.util.importer.job.validation.map.metadata.validation;

import java.util.ArrayList;
import java.util.List;
import javax.validation.ConstraintValidatorContext;
import javax.validation.metadata.ConstraintDescriptor;

import org.apache.commons.logging.Log;
import org.hibernate.validator.engine.MessageAndPath;
import org.hibernate.validator.engine.PathImpl;
import org.hibernate.validator.util.LoggerFactory;
import org.jasig.ssp.util.importer.job.validation.map.metadata.MapConstraintDescriptor;



public class MapConstraintValidatorContext implements
        ConstraintValidatorContext {

    //private static final Log log = LoggerFactory.make();

    private final List<MessageAndPath> messageAndPaths = new ArrayList<MessageAndPath>( 3 );
    private final PathImpl basePath;
    private final ConstraintDescriptor<?> constraintDescriptor;
    private boolean defaultDisabled;
    private MapConstraintDescriptor mapDescriptor;


    public MapConstraintValidatorContext(MapConstraintDescriptor mapDescriptor,PathImpl propertyPath, ConstraintDescriptor<?> constraintDescriptor) {
        this.basePath = PathImpl.createCopy( propertyPath );
        this.constraintDescriptor = constraintDescriptor;
        this.mapDescriptor = mapDescriptor;
    }

    public final void disableDefaultConstraintViolation() {
        defaultDisabled = true;
    }

    public final String getDefaultConstraintMessageTemplate() {
        return mapDescriptor.getMessage( );
    }

    public final ConstraintViolationBuilder buildConstraintViolationWithTemplate(String messageTemplate) {
        return new ErrorBuilderImpl( messageTemplate, PathImpl.createCopy( basePath ) );
    }



    public final List<MessageAndPath> getMessageAndPathList() {
        if ( defaultDisabled && messageAndPaths.size() == 0 ) {
            //throw log.getAtLeastOneCustomMessageMustBeCreatedException();
        }

        List<MessageAndPath> returnedMessageAndPaths = new ArrayList<MessageAndPath>( messageAndPaths );
        if ( !defaultDisabled ) {
            returnedMessageAndPaths.add(
                    new MessageAndPath( getDefaultConstraintMessageTemplate(), basePath )
            );
        }
        return returnedMessageAndPaths;
    }

    class ErrorBuilderImpl implements ConstraintViolationBuilder {
        private String messageTemplate;
        private PathImpl propertyPath;

        ErrorBuilderImpl(String template, PathImpl path) {
            messageTemplate = template;
            propertyPath = path;
        }

        public NodeBuilderDefinedContext addNode(String name) {
            propertyPath.addNode( name );
            return new NodeBuilderImpl( messageTemplate, propertyPath );
        }

        public ConstraintValidatorContext addConstraintViolation() {
            messageAndPaths.add( new MessageAndPath( messageTemplate, propertyPath ) );
            return MapConstraintValidatorContext.this;
        }
    }

    class NodeBuilderImpl implements ConstraintViolationBuilder.NodeBuilderDefinedContext {
        private final String messageTemplate;
        private final PathImpl propertyPath;

        NodeBuilderImpl(String template, PathImpl path) {
            messageTemplate = template;
            propertyPath = path;
        }

        public ConstraintViolationBuilder.NodeBuilderCustomizableContext addNode(String name) {
            return new InIterableNodeBuilderImpl( messageTemplate, propertyPath, name );
        }

        public ConstraintValidatorContext addConstraintViolation() {
            messageAndPaths.add( new MessageAndPath( messageTemplate, propertyPath ) );
            return MapConstraintValidatorContext.this;
        }
    }

    class InIterableNodeBuilderImpl implements ConstraintViolationBuilder.NodeBuilderCustomizableContext {
        private final String messageTemplate;
        private final PathImpl propertyPath;
        private final String leafNodeName;

        InIterableNodeBuilderImpl(String template, PathImpl path, String nodeName) {
            this.messageTemplate = template;
            this.propertyPath = path;
            this.leafNodeName = nodeName;
        }

        public ConstraintViolationBuilder.NodeContextBuilder inIterable() {
            this.propertyPath.makeLeafNodeIterable();
            return new InIterablePropertiesBuilderImpl( messageTemplate, propertyPath, leafNodeName );
        }

        public ConstraintViolationBuilder.NodeBuilderCustomizableContext addNode(String name) {
            propertyPath.addNode( leafNodeName );
            return new InIterableNodeBuilderImpl( messageTemplate, propertyPath, name );
        }

        public ConstraintValidatorContext addConstraintViolation() {
            propertyPath.addNode( leafNodeName );
            messageAndPaths.add( new MessageAndPath( messageTemplate, propertyPath ) );
            return MapConstraintValidatorContext.this;
        }
    }

    class InIterablePropertiesBuilderImpl implements ConstraintViolationBuilder.NodeContextBuilder {
        private final String messageTemplate;
        private final PathImpl propertyPath;
        private final String leafNodeName;

        InIterablePropertiesBuilderImpl(String template, PathImpl path, String nodeName) {
            this.messageTemplate = template;
            this.propertyPath = path;
            this.leafNodeName = nodeName;
        }

        public ConstraintViolationBuilder.NodeBuilderDefinedContext atKey(Object key) {
            propertyPath.setLeafNodeMapKey( key );
            propertyPath.addNode( leafNodeName );
            return new NodeBuilderImpl( messageTemplate, propertyPath );
        }

        public ConstraintViolationBuilder.NodeBuilderDefinedContext atIndex(Integer index) {
            propertyPath.setLeafNodeIndex( index );
            propertyPath.addNode( leafNodeName );
            return new NodeBuilderImpl( messageTemplate, propertyPath );
        }

        public ConstraintViolationBuilder.NodeBuilderCustomizableContext addNode(String name) {
            propertyPath.addNode( leafNodeName );
            return new InIterableNodeBuilderImpl( messageTemplate, propertyPath, name );
        }

        public ConstraintValidatorContext addConstraintViolation() {
            propertyPath.addNode( leafNodeName );
            messageAndPaths.add( new MessageAndPath( messageTemplate, propertyPath ) );
            return MapConstraintValidatorContext.this;
        }
    }

}
