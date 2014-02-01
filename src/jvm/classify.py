import nltk
import sys

#Original credit goes to:
# https://gist.github.com/322906/90dea659c04570757cccf0ce1e6d26c9d06f9283
# Adapted for command line usage

def identify(raw_input):
#raw_input = sys.argv[1]
    sentences = nltk.sent_tokenize(raw_input)
    tokenized_sentences = [nltk.word_tokenize(sentence) for sentence in sentences]
    tagged_sentences = [nltk.pos_tag(sentence) for sentence in tokenized_sentences]
    chunked_sentences = nltk.batch_ne_chunk(tagged_sentences, binary=True)

    def extract_entity_names(t):
        entity_names = []
        
        if hasattr(t, 'node') and t.node:
            if t.node == 'NE':
                entity_names.append(' '.join([child[0] for child in t]))
            else:
                for child in t:
                    entity_names.extend(extract_entity_names(child))
        
        return entity_names

    entity_names = []
    for tree in chunked_sentences:
        # Print results per sentence
        # print extract_entity_names(tree)
        
        entity_names.extend(extract_entity_names(tree))

    # Print all entity names
    #print entity_names

    # Print unique entity names
    print set(entity_names)


if __name__ == "__main__":
    identify("My name is Tristan")