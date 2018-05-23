OPUS_SOURCES = src/opus.c \
src/opus_decoder.c \
src/opus_encoder.c \
src/opus_multistream.c \
src/opus_multistream_encoder.c \
src/opus_multistream_decoder.c \
src/repacketizer.c \
opustool/resample.c \
opustool/com_xtc_audio_util_OpusEncoder.c \
opustool/com_xtc_audio_util_OpusDecoder.c

OPUS_SOURCES_FLOAT = \
src/analysis.c \
src/mlp.c \
src/mlp_data.c
